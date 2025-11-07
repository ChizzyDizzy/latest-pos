#!/bin/bash

#####################################################################
# SYOS POS System - Automated Test Suite Runner
#
# This script runs all tests in sequence:
# 1. Unit Tests (JUnit + Mockito)
# 2. Application Build
# 3. Start Tomcat
# 4. API Tests (Postman/Newman)
# 5. Performance Tests (JMeter - 200 concurrent users)
# 6. Generate Reports
# 7. Cleanup
#
# Usage: ./run-all-tests.sh [options]
# Options:
#   --skip-unit        Skip unit tests
#   --skip-api         Skip API tests
#   --skip-perf        Skip performance tests
#   --quick            Run quick tests (50 users instead of 200)
#####################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test flags
SKIP_UNIT=false
SKIP_API=false
SKIP_PERF=false
QUICK_MODE=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-unit)
            SKIP_UNIT=true
            shift
            ;;
        --skip-api)
            SKIP_API=true
            shift
            ;;
        --skip-perf)
            SKIP_PERF=true
            shift
            ;;
        --quick)
            QUICK_MODE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--skip-unit] [--skip-api] [--skip-perf] [--quick]"
            exit 1
            ;;
    esac
done

# Print header
echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║                                                              ║"
echo "║         SYOS POS System - Automated Test Suite              ║"
echo "║                                                              ║"
echo "║  Testing webapp with 200 concurrent users                   ║"
echo "║  Performance • API • Unit Testing                           ║"
echo "║                                                              ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Configuration
PROJECT_ROOT="/home/user/latest-pos"
RESULTS_DIR="$PROJECT_ROOT/testing/results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Create results directory
mkdir -p "$RESULTS_DIR"

# Log file
LOG_FILE="$RESULTS_DIR/test-run-$TIMESTAMP.log"

# Function to log messages
log() {
    echo -e "$1" | tee -a "$LOG_FILE"
}

# Function to print step header
step_header() {
    echo ""
    log "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    log "${BLUE}  $1${NC}"
    log "${BLUE}═══════════════════════════════════════════════════════════${NC}"
}

# Function to check command exists
check_command() {
    if ! command -v $1 &> /dev/null; then
        log "${RED}✗ $1 is not installed${NC}"
        log "${YELLOW}  Please install $1 and try again${NC}"
        exit 1
    fi
}

# Verify prerequisites
step_header "Step 0: Verifying Prerequisites"

log "${YELLOW}Checking required tools...${NC}"
check_command java
check_command mvn
check_command mysql

log "${GREEN}✓ Java: $(java -version 2>&1 | head -n 1)${NC}"
log "${GREEN}✓ Maven: $(mvn --version | head -n 1)${NC}"
log "${GREEN}✓ MySQL: $(mysql --version)${NC}"

# Optional tools
if command -v newman &> /dev/null; then
    log "${GREEN}✓ Newman: $(newman --version)${NC}"
else
    log "${YELLOW}⚠ Newman not found - API tests will be skipped${NC}"
    SKIP_API=true
fi

if command -v jmeter &> /dev/null; then
    log "${GREEN}✓ JMeter: Available${NC}"
elif [ -f "$JMETER_HOME/bin/jmeter" ]; then
    log "${GREEN}✓ JMeter: $JMETER_HOME${NC}"
else
    log "${YELLOW}⚠ JMeter not found - Performance tests will be skipped${NC}"
    SKIP_PERF=true
fi

# Verify database
log "${YELLOW}Checking database connection...${NC}"
if mysql -u root -pSportS28 -e "USE syos_db;" 2>/dev/null; then
    log "${GREEN}✓ Database 'syos_db' is accessible${NC}"
else
    log "${RED}✗ Cannot connect to database 'syos_db'${NC}"
    log "${YELLOW}  Please run: mysql -u root -p < sql/create_database.sql${NC}"
    exit 1
fi

# Change to project root
cd "$PROJECT_ROOT"

#####################################################################
# STEP 1: Unit Tests
#####################################################################
if [ "$SKIP_UNIT" = false ]; then
    step_header "Step 1: Running Unit Tests"

    log "${YELLOW}Executing JUnit tests...${NC}"

    if mvn clean test >> "$LOG_FILE" 2>&1; then
        log "${GREEN}✓ Unit tests passed${NC}"

        # Generate coverage report
        log "${YELLOW}Generating code coverage report...${NC}"
        if mvn jacoco:report >> "$LOG_FILE" 2>&1; then
            log "${GREEN}✓ Coverage report generated${NC}"
            log "${BLUE}  Report: pos-web/target/site/jacoco/index.html${NC}"
        fi
    else
        log "${RED}✗ Unit tests failed${NC}"
        log "${YELLOW}  Check log: $LOG_FILE${NC}"
        exit 1
    fi
else
    log "${YELLOW}⊘ Unit tests skipped${NC}"
fi

#####################################################################
# STEP 2: Build Application
#####################################################################
step_header "Step 2: Building Application"

log "${YELLOW}Building with Maven...${NC}"

if mvn clean install -DskipTests >> "$LOG_FILE" 2>&1; then
    log "${GREEN}✓ Build successful${NC}"
else
    log "${RED}✗ Build failed${NC}"
    log "${YELLOW}  Check log: $LOG_FILE${NC}"
    exit 1
fi

#####################################################################
# STEP 3: Start Tomcat
#####################################################################
step_header "Step 3: Starting Application Server"

log "${YELLOW}Starting Tomcat with webapp...${NC}"

cd pos-web

# Start Tomcat in background
mvn tomcat9:run >> "$LOG_FILE" 2>&1 &
TOMCAT_PID=$!

log "${BLUE}  Tomcat PID: $TOMCAT_PID${NC}"

# Wait for Tomcat to start
log "${YELLOW}Waiting for Tomcat to start...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8080/pos/login > /dev/null 2>&1; then
        log "${GREEN}✓ Tomcat started successfully${NC}"
        break
    fi

    if [ $i -eq 30 ]; then
        log "${RED}✗ Tomcat failed to start within 30 seconds${NC}"
        kill $TOMCAT_PID 2>/dev/null
        exit 1
    fi

    echo -n "."
    sleep 1
done
echo ""

cd "$PROJECT_ROOT"

#####################################################################
# STEP 4: API Tests (Postman/Newman)
#####################################################################
if [ "$SKIP_API" = false ]; then
    step_header "Step 4: Running API Tests (Postman/Newman)"

    log "${YELLOW}Executing Postman collection...${NC}"

    newman run testing/postman/SYOS_API_Tests.postman_collection.json \
           --reporters cli,htmlextra \
           --reporter-htmlextra-export "$RESULTS_DIR/api-test-report-$TIMESTAMP.html" \
           >> "$LOG_FILE" 2>&1

    API_EXIT_CODE=$?

    if [ $API_EXIT_CODE -eq 0 ]; then
        log "${GREEN}✓ API tests passed${NC}"
        log "${BLUE}  Report: $RESULTS_DIR/api-test-report-$TIMESTAMP.html${NC}"
    else
        log "${YELLOW}⚠ Some API tests failed${NC}"
        log "${BLUE}  Report: $RESULTS_DIR/api-test-report-$TIMESTAMP.html${NC}"
    fi
else
    log "${YELLOW}⊘ API tests skipped${NC}"
fi

#####################################################################
# STEP 5: Performance Tests (JMeter)
#####################################################################
if [ "$SKIP_PERF" = false ]; then
    step_header "Step 5: Running Performance Tests (JMeter)"

    if [ "$QUICK_MODE" = true ]; then
        log "${YELLOW}Running in QUICK mode (50 concurrent users)...${NC}"
        NUM_USERS=50
    else
        log "${YELLOW}Running FULL test (200 concurrent users)...${NC}"
        NUM_USERS=200
    fi

    cd testing/jmeter
    mkdir -p results

    # Determine JMeter command
    if command -v jmeter &> /dev/null; then
        JMETER_CMD="jmeter"
    elif [ -f "$JMETER_HOME/bin/jmeter" ]; then
        JMETER_CMD="$JMETER_HOME/bin/jmeter"
    else
        log "${RED}✗ JMeter not found${NC}"
        cd "$PROJECT_ROOT"
        kill $TOMCAT_PID 2>/dev/null
        exit 1
    fi

    log "${YELLOW}Starting JMeter test...${NC}"
    log "${BLUE}  This may take several minutes...${NC}"

    $JMETER_CMD -n -t SYOS_Performance_Test_200_Users.jmx \
           -l "results/results-$TIMESTAMP.jtl" \
           -e -o "results/html-report-$TIMESTAMP" \
           -JNUM_USERS=$NUM_USERS \
           >> "$LOG_FILE" 2>&1

    JMETER_EXIT_CODE=$?

    if [ $JMETER_EXIT_CODE -eq 0 ]; then
        log "${GREEN}✓ Performance tests completed${NC}"
        log "${BLUE}  Report: testing/jmeter/results/html-report-$TIMESTAMP/index.html${NC}"

        # Parse results
        log ""
        log "${BLUE}Performance Summary:${NC}"

        # Extract key metrics (if jtl file exists)
        if [ -f "results/results-$TIMESTAMP.jtl" ]; then
            TOTAL_SAMPLES=$(grep -c "^[0-9]" "results/results-$TIMESTAMP.jtl" || echo "N/A")
            log "${BLUE}  Total Requests: $TOTAL_SAMPLES${NC}"
        fi
    else
        log "${YELLOW}⚠ Performance tests completed with issues${NC}"
        log "${BLUE}  Report: testing/jmeter/results/html-report-$TIMESTAMP/index.html${NC}"
    fi

    cd "$PROJECT_ROOT"
else
    log "${YELLOW}⊘ Performance tests skipped${NC}"
fi

#####################################################################
# STEP 6: Cleanup
#####################################################################
step_header "Step 6: Cleanup"

log "${YELLOW}Stopping Tomcat...${NC}"

if kill $TOMCAT_PID 2>/dev/null; then
    log "${GREEN}✓ Tomcat stopped (PID: $TOMCAT_PID)${NC}"
else
    log "${YELLOW}⚠ Tomcat may have already stopped${NC}"
fi

# Wait for process to fully terminate
sleep 2

#####################################################################
# Final Summary
#####################################################################
echo ""
log "${GREEN}════════════════════════════════════════════════════════════${NC}"
log "${GREEN}  ✓ All Tests Completed Successfully!${NC}"
log "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo ""

log "${BLUE}📊 Test Reports:${NC}"
log "${BLUE}├─ Unit Tests Coverage:${NC}"
log "${BLUE}│  └─ pos-web/target/site/jacoco/index.html${NC}"

if [ "$SKIP_API" = false ]; then
    log "${BLUE}├─ API Tests:${NC}"
    log "${BLUE}│  └─ $RESULTS_DIR/api-test-report-$TIMESTAMP.html${NC}"
fi

if [ "$SKIP_PERF" = false ]; then
    log "${BLUE}├─ Performance Tests:${NC}"
    log "${BLUE}│  └─ testing/jmeter/results/html-report-$TIMESTAMP/index.html${NC}"
fi

log "${BLUE}└─ Full Log:${NC}"
log "${BLUE}   └─ $LOG_FILE${NC}"

echo ""
log "${YELLOW}💡 Tips:${NC}"
log "${YELLOW}  • Open reports in your browser for detailed analysis${NC}"
log "${YELLOW}  • Check $LOG_FILE for detailed execution logs${NC}"
log "${YELLOW}  • Run with --quick flag for faster testing (50 users)${NC}"
log "${YELLOW}  • Use --skip-* flags to skip specific test types${NC}"

echo ""
log "${GREEN}✅ Testing complete! ${NC}"
echo ""

exit 0
