# SYOS POS System - Comprehensive Testing Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [Test Environment Setup](#test-environment-setup)
3. [Performance Testing with JMeter](#performance-testing-with-jmeter)
4. [API Testing with Postman](#api-testing-with-postman)
5. [Unit Testing](#unit-testing)
6. [Tomcat Configuration](#tomcat-configuration)
7. [Database Setup for Testing](#database-setup-for-testing)
8. [Running Tests](#running-tests)
9. [Test Reports and Analysis](#test-reports-and-analysis)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This testing suite provides comprehensive testing coverage for the SYOS POS System webapp including:

- **Performance Testing**: 200 concurrent users simulation with Apache JMeter
- **API Testing**: Complete REST API endpoint validation with Postman
- **Unit Testing**: JUnit 5 + Mockito servlet and service layer tests
- **Load Testing**: Thread pool and connection pool stress testing
- **Integration Testing**: End-to-end user journey validation

### Testing Scope
- ✅ **Webapp Only** - CLI application is excluded from testing
- ✅ **All User Roles** - Admin, Cashier, Customer scenarios
- ✅ **Concurrent Operations** - Simulates real-world multi-user scenarios
- ✅ **Performance Metrics** - Response times, throughput, error rates

---

## 🛠️ Test Environment Setup

### Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   # Should show version 11 or higher
   ```

2. **Apache Maven 3.6+**
   ```bash
   mvn --version
   ```

3. **Apache JMeter 5.5+**
   - Download from: https://jmeter.apache.org/download_jmeter.cgi
   - Extract to a directory (e.g., `/opt/jmeter` or `C:\jmeter`)
   - Add JMeter `bin` directory to your PATH

4. **Postman**
   - Download from: https://www.postman.com/downloads/
   - Or use Postman CLI (Newman) for command-line execution:
   ```bash
   npm install -g newman
   npm install -g newman-reporter-htmlextra
   ```

5. **MySQL Database**
   - Version 8.0+ recommended
   - Running on localhost:3306

### Environment Variables

```bash
# JMeter Home
export JMETER_HOME=/path/to/jmeter
export PATH=$JMETER_HOME/bin:$PATH

# Java Heap Size for JMeter (for large tests)
export HEAP="-Xms1g -Xmx1g"
```

### Database Setup

1. **Create Test Database**
   ```bash
   cd /home/user/latest-pos/sql
   mysql -u root -p < create_database.sql
   mysql -u root -p syos_db < seed_data.sql
   ```

2. **Verify Database Connection**
   ```bash
   mysql -u root -p syos_db
   # Test credentials from application.properties
   # Default: username=root, password=SportS28
   ```

3. **Test User Accounts** (pre-populated in database)
   - **Admin**: admin / admin123
   - **Cashier**: cashier1 / cashier123
   - **Manager**: manager1 / manager123
   - **Customer**: customer1 / customer123

---

## 🚀 Performance Testing with JMeter

### Test Plan Overview

The JMeter test plan (`SYOS_Performance_Test_200_Users.jmx`) simulates 200 concurrent users:

- **40 Admin Users** (20%) - Dashboard and management operations
- **80 Cashier Users** (40%) - Sales and billing operations
- **80 Customer Users** (40%) - Product browsing and purchasing

### Configuration Details

```xml
Thread Groups:
├── Admin Users
│   ├── Threads: 40
│   ├── Ramp-up: 60 seconds
│   ├── Loops: 5
│   └── Operations: Login → Dashboard → Inventory → Reports
│
├── Cashier Users
│   ├── Threads: 80
│   ├── Ramp-up: 60 seconds
│   ├── Loops: 10
│   └── Operations: Login → Sales → Add Items → Complete Sale
│
└── Customer Users
    ├── Threads: 80
    ├── Ramp-up: 60 seconds
    ├── Loops: 15
    └── Operations: Browse Products → View Details
```

### Running JMeter Tests

#### 1. GUI Mode (Development/Debugging)

```bash
cd /home/user/latest-pos/testing/jmeter

# Launch JMeter GUI
jmeter

# In JMeter GUI:
# File → Open → Select SYOS_Performance_Test_200_Users.jmx
# Click green "Start" button (Ctrl+R)
```

**⚠️ Warning**: GUI mode is resource-intensive. Use only for test development/debugging.

#### 2. CLI Mode (Production Testing) - **RECOMMENDED**

```bash
cd /home/user/latest-pos/testing/jmeter

# Basic run with console output
jmeter -n -t SYOS_Performance_Test_200_Users.jmx -l results/results.jtl

# Generate HTML dashboard report
jmeter -n -t SYOS_Performance_Test_200_Users.jmx \
       -l results/results.jtl \
       -e -o results/html-report

# With custom properties
jmeter -n -t SYOS_Performance_Test_200_Users.jmx \
       -l results/results.jtl \
       -JNUM_USERS=200 \
       -JRAMP_UP_TIME=60 \
       -JBASE_URL=http://localhost:8080/pos \
       -e -o results/html-report
```

#### 3. Distributed Testing (Multiple Machines)

```bash
# On master machine
jmeter -n -t SYOS_Performance_Test_200_Users.jmx \
       -R server1,server2,server3 \
       -l results/distributed_results.jtl

# On each slave machine (run first)
jmeter-server
```

### Test Scenarios Covered

#### Admin Scenarios
1. **Login Authentication**
   - POST /login with admin credentials
   - Session cookie validation
   - Response time < 2 seconds

2. **Dashboard Access**
   - GET /dashboard
   - Metrics calculation (today's sales, revenue)
   - Response time < 3 seconds

3. **Inventory Management**
   - GET /inventory (view all items)
   - POST /inventory/add-stock (add new stock)
   - Response time < 2 seconds

4. **Reports Generation**
   - GET /reports/daily-sales
   - GET /reports/stock
   - Response time < 3 seconds

#### Cashier Scenarios
1. **Sales Operations**
   - GET /sales (sales page)
   - GET /sales/available-items (JSON API)
   - POST /sales/add-item (add to cart)
   - POST /sales/remove-item (remove from cart)
   - POST /sales/complete (finalize sale)

2. **Billing**
   - GET /sales/bills (view all bills)
   - Response time < 2 seconds

#### Customer Scenarios
1. **Product Browsing** (No Authentication Required)
   - GET /products (browse catalog)
   - GET /products/{id} (product details)
   - Response time < 1.5 seconds

### Performance Assertions

```
Expected Results:
├── Response Time
│   ├── Average: < 2 seconds
│   ├── 90th percentile: < 3 seconds
│   └── 95th percentile: < 5 seconds
│
├── Throughput
│   ├── Minimum: 50 requests/second
│   └── Target: 100+ requests/second
│
├── Error Rate
│   └── < 1% (less than 1 error per 100 requests)
│
└── Concurrent Users
    └── 200 simultaneous active sessions
```

### Understanding Results

After test completion, check:

1. **Summary Report**
   - Samples: Total number of requests
   - Average: Average response time
   - Min/Max: Response time range
   - Error %: Percentage of failed requests
   - Throughput: Requests per second

2. **HTML Dashboard** (`results/html-report/index.html`)
   - Statistics (response times, throughput)
   - Charts (over time analysis)
   - Top 5 errors
   - Response times percentiles

3. **Response Time Graph**
   - Identify performance degradation patterns
   - Spot bottlenecks during peak load

---

## 🔌 API Testing with Postman

### Postman Collection Overview

The Postman collection (`SYOS_API_Tests.postman_collection.json`) includes:

- 40+ API endpoint tests
- Authentication flows
- CRUD operations
- JSON API validation
- Concurrent user simulation

### Importing Collection

#### Method 1: Postman Desktop App

1. Open Postman
2. Click **Import** button
3. Select file: `/home/user/latest-pos/testing/postman/SYOS_API_Tests.postman_collection.json`
4. Click **Import**

#### Method 2: Command Line (Newman)

```bash
# Install Newman if not already installed
npm install -g newman
npm install -g newman-reporter-htmlextra

# Run collection
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --environment testing/postman/local-environment.json \
       --reporters cli,htmlextra \
       --reporter-htmlextra-export results/postman-report.html

# Run with 200 iterations (concurrent users simulation)
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --iteration-count 200 \
       --delay-request 100 \
       --reporters cli,htmlextra \
       --reporter-htmlextra-export results/concurrent-test.html
```

### Test Categories

#### 1. Authentication (5 tests)
- Admin Login
- Cashier Login
- Customer Login
- Invalid Login (negative test)
- Logout

#### 2. Dashboard & Reports (3 tests)
- View Dashboard
- Daily Sales Report
- Stock Report

#### 3. Sales Operations (6 tests)
- View Sales Page
- Get Available Items (JSON API)
- Add Item to Sale (JSON API)
- Remove Item from Sale (JSON API)
- Complete Sale
- View Bills

#### 4. Inventory Management (5 tests)
- View Inventory
- Add Stock
- Move to Shelf (JSON API)
- Low Stock Items
- Expiring Items

#### 5. Product Catalog (3 tests)
- Browse Products (public)
- View Product Details
- Add Product (admin only)

#### 6. User Management (2 tests)
- View Users (admin)
- Add User (admin)

#### 7. Concurrent Load Testing (1 test)
- Concurrent Login Test (run with Collection Runner)

### Running Specific Test Groups

```bash
# Run only authentication tests
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --folder "Authentication"

# Run only sales operations
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --folder "Sales Operations"

# Run all tests with specific environment
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --env-var "base_url=http://localhost:8080/pos"
```

### Collection Variables

```json
{
  "base_url": "http://localhost:8080/pos",
  "admin_session": "",      // Auto-populated after admin login
  "cashier_session": "",    // Auto-populated after cashier login
  "customer_session": ""    // Auto-populated after customer login
}
```

### Test Assertions

Each request includes:

1. **Status Code Assertion**
   ```javascript
   pm.test("Status code is 200", function () {
       pm.response.to.have.status(200);
   });
   ```

2. **Response Time Assertion**
   ```javascript
   pm.test("Response time < 2000ms", function () {
       pm.expect(pm.response.responseTime).to.be.below(2000);
   });
   ```

3. **Content Validation**
   ```javascript
   pm.test("Response is JSON", function () {
       pm.response.to.be.json;
   });
   ```

4. **Data Structure Validation**
   ```javascript
   pm.test("Required properties present", function () {
       var jsonData = pm.response.json();
       pm.expect(jsonData).to.have.property('id');
       pm.expect(jsonData).to.have.property('name');
   });
   ```

---

## 🧪 Unit Testing

### Test Structure

```
pos-web/
└── src/
    ├── main/java/     (production code)
    └── test/java/     (unit tests - TO BE CREATED)
        └── com/syos/web/
            ├── controllers/
            │   ├── LoginServletTest.java
            │   ├── DashboardServletTest.java
            │   ├── SalesServletTest.java
            │   └── ...
            ├── filters/
            │   ├── AuthenticationFilterTest.java
            │   └── SecurityHeadersFilterTest.java
            └── listeners/
                └── AppContextListenerTest.java
```

### Creating Unit Tests

The project uses:
- **JUnit 5** (Jupiter) for test framework
- **Mockito 5** for mocking
- **Maven Surefire** for test execution

### Sample Test Template

```java
package com.syos.web.controllers;

import org.junit.jupiter.api.*;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

class LoginServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private LoginServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should redirect to dashboard when user already logged in")
    void testDoGet_AlreadyLoggedIn() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("username")).thenReturn("admin");

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(response).sendRedirect("/dashboard");
    }

    @Test
    @DisplayName("Should show login page when not logged in")
    void testDoGet_NotLoggedIn() throws ServletException, IOException {
        // Arrange
        when(request.getSession(false)).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/login.jsp"))
            .thenReturn(dispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should create session on valid credentials")
    void testDoPost_ValidCredentials() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("admin123");
        when(request.getSession()).thenReturn(session);

        // Mock UserDao validation (you'll need to inject this)
        // when(userDao.validateCredentials("admin", "admin123")).thenReturn(true);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(session).setAttribute("username", "admin");
        verify(response).sendRedirect(contains("dashboard"));
    }

    @Test
    @DisplayName("Should reject invalid credentials")
    void testDoPost_InvalidCredentials() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("wrongpassword");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response, never()).sendRedirect(anyString());
        verify(request).setAttribute(eq("error"), anyString());
    }
}
```

### Running Unit Tests

```bash
# Run all tests
cd /home/user/latest-pos
mvn test

# Run tests for specific module
cd pos-web
mvn test

# Run specific test class
mvn test -Dtest=LoginServletTest

# Run with coverage report (requires JaCoCo plugin)
mvn clean test jacoco:report

# Skip tests during build
mvn clean install -DskipTests
```

### Test Coverage Goals

```
Target Coverage:
├── Servlets: 80%+
├── Filters: 90%+
├── Listeners: 80%+
├── DAOs: 75%+
└── Services: 85%+
```

---

## ⚙️ Tomcat Configuration

### Thread Pool Configuration

The `server.xml` file in `/testing/config/` is optimized for 200 concurrent users.

**Key Configuration Parameters:**

```xml
<Connector port="8080" protocol="HTTP/1.1"
           maxThreads="200"        <!-- Max worker threads -->
           minSpareThreads="25"    <!-- Min idle threads -->
           acceptCount="100"       <!-- Request queue size -->
           maxConnections="250"    <!-- Max simultaneous connections -->
           connectionTimeout="20000" <!-- 20 second timeout -->
           compression="on"        <!-- Enable HTTP compression -->
           maxKeepAliveRequests="100" <!-- Keep-alive requests per connection -->
           keepAliveTimeout="5000" />  <!-- 5 second keep-alive timeout -->
```

### Configuration Explanation

1. **maxThreads="200"**
   - Maximum number of request processing threads
   - Directly supports 200 concurrent users
   - Each thread handles one request at a time

2. **acceptCount="100"**
   - Queue size for incoming requests when all threads are busy
   - Prevents "Connection refused" errors during traffic spikes
   - Total capacity = maxThreads (200) + acceptCount (100) = 300

3. **maxConnections="250"**
   - Maximum number of connections server will accept
   - Set higher than maxThreads for connection pooling

4. **compression="on"**
   - Compresses HTML, CSS, JS, JSON responses
   - Reduces bandwidth by 60-80%
   - Improves response times for slow connections

### Applying Configuration

#### Option 1: Copy to Tomcat Installation

```bash
# Backup existing config
cp $CATALINA_HOME/conf/server.xml $CATALINA_HOME/conf/server.xml.backup

# Copy optimized config
cp /home/user/latest-pos/testing/config/server.xml $CATALINA_HOME/conf/

# Restart Tomcat
$CATALINA_HOME/bin/shutdown.sh
$CATALINA_HOME/bin/startup.sh
```

#### Option 2: Maven Tomcat Plugin

Update `pos-web/pom.xml`:

```xml
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat9-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8080</port>
        <path>/pos</path>
        <systemProperties>
            <JAVA_OPTS>
                -Xms512m -Xmx1024m
                -XX:MaxPermSize=256m
            </JAVA_OPTS>
        </systemProperties>
        <!-- Server configuration -->
        <server>tomcat9</server>
        <configurationDir>${project.basedir}/../testing/config</configurationDir>
    </configuration>
</plugin>
```

### Monitoring Thread Pool

```bash
# Check Tomcat thread status
curl http://localhost:8080/manager/status/all

# Monitor active threads (requires manager app)
curl --user admin:password \
     http://localhost:8080/manager/text/status

# View thread dump
jstack <tomcat_pid> > threaddump.txt
```

---

## 💾 Database Setup for Testing

### Connection Pool Configuration

**Current Settings** (from `application.properties`):

```properties
db.url=jdbc:mysql://localhost:3306/syos_db
db.username=root
db.password=SportS28
db.pool.initial=5    # Initial pool size
db.pool.max=10       # Maximum pool size
```

### Recommended Settings for 200 Concurrent Users

Update `pos-core/src/main/resources/config/application.properties`:

```properties
# Original settings (good for development)
# db.pool.initial=5
# db.pool.max=10

# Optimized for 200 concurrent users
db.pool.initial=20       # Start with 20 connections ready
db.pool.max=50          # Allow up to 50 connections
db.pool.maxIdle=30      # Keep 30 connections idle
db.pool.minIdle=10      # Minimum 10 connections always ready
db.pool.maxWait=30000   # Wait up to 30 seconds for connection
db.pool.testOnBorrow=true           # Validate before use
db.pool.testWhileIdle=true          # Validate idle connections
db.pool.timeBetweenEvictionRunsMillis=60000  # Check every minute
db.pool.minEvictableIdleTimeMillis=300000    # Evict after 5 min idle
```

### MySQL Configuration

For optimal performance, update MySQL configuration (`/etc/mysql/my.cnf` or `my.ini`):

```ini
[mysqld]
# Connection Settings
max_connections=300              # Allow 300 concurrent connections
max_connect_errors=100

# Performance Schema (for monitoring)
performance_schema=ON

# Buffer Pool (adjust based on available RAM)
innodb_buffer_pool_size=1G       # 1GB for buffer pool
innodb_log_file_size=256M
innodb_flush_log_at_trx_commit=2

# Query Cache (deprecated in MySQL 8.0+)
# query_cache_size=64M
# query_cache_type=1

# Slow Query Log (for debugging)
slow_query_log=1
slow_query_log_file=/var/log/mysql/slow-query.log
long_query_time=2                # Log queries taking > 2 seconds
```

### Database Indexes for Performance

Ensure these indexes exist:

```sql
-- Users table
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_role ON users(role);

-- Items table
CREATE INDEX idx_item_name ON items(name);
CREATE INDEX idx_item_category ON items(category);
CREATE INDEX idx_item_state ON items(state);

-- Bills table
CREATE INDEX idx_bill_date ON bills(created_at);
CREATE INDEX idx_bill_cashier ON bills(cashier_id);

-- Bill Items table
CREATE INDEX idx_bill_items_bill ON bill_items(bill_id);
CREATE INDEX idx_bill_items_item ON bill_items(item_id);

-- Stock Movements table
CREATE INDEX idx_stock_movement_date ON stock_movements(movement_date);
CREATE INDEX idx_stock_movement_type ON stock_movements(movement_type);
```

### Test Data Setup

```bash
cd /home/user/latest-pos/sql

# Reset database (WARNING: Deletes all data)
mysql -u root -p < create_database.sql

# Load test data
mysql -u root -p syos_db < seed_data.sql

# Verify data loaded
mysql -u root -p syos_db -e "
    SELECT
        (SELECT COUNT(*) FROM users) as users,
        (SELECT COUNT(*) FROM items) as items,
        (SELECT COUNT(*) FROM bills) as bills,
        (SELECT COUNT(*) FROM stock_movements) as stock_movements;
"
```

---

## 🏃 Running Tests

### Complete Test Execution Workflow

#### Step 1: Start Application

```bash
cd /home/user/latest-pos

# Build the project
mvn clean install

# Start Tomcat with webapp
cd pos-web
mvn tomcat9:run

# Verify application is running
curl http://localhost:8080/pos/login
```

#### Step 2: Run Unit Tests

```bash
# In a new terminal
cd /home/user/latest-pos

# Run all unit tests
mvn test

# Generate coverage report
mvn clean test jacoco:report

# View coverage report
open pos-web/target/site/jacoco/index.html
```

#### Step 3: Run API Tests (Postman/Newman)

```bash
cd /home/user/latest-pos

# Run API tests
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --reporters cli,htmlextra \
       --reporter-htmlextra-export testing/results/api-test-report.html

# View results
open testing/results/api-test-report.html
```

#### Step 4: Run Performance Tests (JMeter)

```bash
cd /home/user/latest-pos/testing/jmeter

# Create results directory
mkdir -p results

# Run JMeter test
jmeter -n -t SYOS_Performance_Test_200_Users.jmx \
       -l results/results.jtl \
       -e -o results/html-report

# View dashboard
open results/html-report/index.html
```

### Automated Test Script

Create `run-all-tests.sh`:

```bash
#!/bin/bash

echo "🚀 Starting SYOS POS System Test Suite"
echo "======================================"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Step 1: Unit Tests
echo -e "\n${GREEN}Step 1: Running Unit Tests${NC}"
mvn clean test
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Unit tests failed${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Unit tests passed${NC}"

# Step 2: Build and Start Application
echo -e "\n${GREEN}Step 2: Building Application${NC}"
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Build successful${NC}"

# Start Tomcat in background
echo -e "\n${GREEN}Step 3: Starting Tomcat${NC}"
cd pos-web
mvn tomcat9:run &
TOMCAT_PID=$!

# Wait for Tomcat to start
sleep 30

# Check if Tomcat is running
curl -s http://localhost:8080/pos/login > /dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Tomcat failed to start${NC}"
    kill $TOMCAT_PID
    exit 1
fi
echo -e "${GREEN}✅ Tomcat started successfully${NC}"

cd ..

# Step 3: API Tests
echo -e "\n${GREEN}Step 4: Running API Tests (Postman)${NC}"
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --reporters cli,htmlextra \
       --reporter-htmlextra-export testing/results/api-test-report.html

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ API tests failed${NC}"
    kill $TOMCAT_PID
    exit 1
fi
echo -e "${GREEN}✅ API tests passed${NC}"

# Step 4: Performance Tests
echo -e "\n${GREEN}Step 5: Running Performance Tests (JMeter)${NC}"
cd testing/jmeter
mkdir -p results

jmeter -n -t SYOS_Performance_Test_200_Users.jmx \
       -l results/results.jtl \
       -e -o results/html-report

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Performance tests failed${NC}"
    kill $TOMCAT_PID
    exit 1
fi
echo -e "${GREEN}✅ Performance tests passed${NC}"

# Cleanup
echo -e "\n${GREEN}Step 6: Cleanup${NC}"
kill $TOMCAT_PID
echo -e "${GREEN}✅ Tomcat stopped${NC}"

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}✅ All tests completed successfully!${NC}"
echo -e "${GREEN}========================================${NC}"

echo -e "\n📊 Test Reports:"
echo -e "  - API Tests: testing/results/api-test-report.html"
echo -e "  - Performance Tests: testing/jmeter/results/html-report/index.html"
echo -e "  - Unit Test Coverage: pos-web/target/site/jacoco/index.html"
```

Make executable:
```bash
chmod +x run-all-tests.sh
./run-all-tests.sh
```

---

## 📊 Test Reports and Analysis

### JMeter HTML Dashboard

After running JMeter tests, open `testing/jmeter/results/html-report/index.html`

**Key Metrics to Check:**

1. **Statistics**
   - Total Samples
   - Error Rate (should be < 1%)
   - Average Response Time
   - Min/Max Response Times
   - Throughput (requests/second)

2. **Response Time Percentiles**
   - 50th percentile (median)
   - 90th percentile
   - 95th percentile
   - 99th percentile

3. **Active Threads Over Time**
   - Shows thread ramp-up
   - Identifies if full load was reached

4. **Response Time vs Request**
   - Identifies performance degradation patterns
   - Shows if response time increases with load

### Postman/Newman Reports

After running Newman, open `testing/results/api-test-report.html`

**Key Sections:**

1. **Summary**
   - Total Requests
   - Total Tests
   - Test Pass Rate
   - Average Response Time

2. **Failed Tests**
   - Lists all failed assertions
   - Shows request/response details
   - Helps identify issues

3. **Requests**
   - Individual request details
   - Response codes
   - Response times

### Performance Benchmarks

**Acceptable Performance Criteria:**

```yaml
Response Times:
  Average: < 2000ms
  90th Percentile: < 3000ms
  95th Percentile: < 5000ms
  99th Percentile: < 8000ms

Throughput:
  Minimum: 50 req/sec
  Target: 100+ req/sec
  Peak: 150+ req/sec

Error Rate:
  Maximum: 1%
  Target: < 0.5%

Resource Utilization:
  CPU: < 80%
  Memory: < 80%
  Database Connections: < 80% of pool size
  Thread Pool: < 80% of maxThreads
```

### Analyzing Performance Issues

**Common Issues and Solutions:**

1. **High Response Times**
   - Check database query performance
   - Verify database indexes
   - Review connection pool settings
   - Check for N+1 query problems

2. **High Error Rate**
   - Check Tomcat logs: `catalina.out`
   - Review database connection timeout
   - Verify thread pool size
   - Check for deadlocks

3. **Low Throughput**
   - Increase thread pool size
   - Optimize database queries
   - Enable HTTP compression
   - Add caching (if applicable)

4. **Memory Issues**
   - Check for memory leaks
   - Increase JVM heap size
   - Review object creation patterns
   - Monitor garbage collection

### Monitoring Commands

```bash
# Monitor Tomcat CPU and Memory
top -p $(pgrep -f tomcat)

# Monitor MySQL connections
mysql -u root -p -e "SHOW STATUS LIKE 'Threads_connected';"
mysql -u root -p -e "SHOW PROCESSLIST;"

# Monitor slow queries
mysql -u root -p -e "SHOW FULL PROCESSLIST;"

# Check Tomcat logs
tail -f $CATALINA_HOME/logs/catalina.out

# Monitor disk I/O
iostat -x 2

# Network monitoring
netstat -an | grep 8080 | wc -l
```

---

## 🔧 Troubleshooting

### Common Issues

#### 1. "Connection refused" when running tests

**Problem**: Application not started or wrong port

**Solution**:
```bash
# Check if Tomcat is running
ps aux | grep tomcat

# Check if port 8080 is in use
lsof -i :8080
netstat -an | grep 8080

# Start application
cd pos-web
mvn tomcat9:run
```

#### 2. "Too many connections" database error

**Problem**: Database connection pool exhausted

**Solution**:
```bash
# Check current connections
mysql -u root -p -e "SHOW STATUS LIKE 'Threads_connected';"

# Increase pool size in application.properties
db.pool.max=50

# Increase MySQL max_connections
# Edit /etc/mysql/my.cnf
max_connections=300
```

#### 3. JMeter "OutOfMemoryError"

**Problem**: Insufficient heap size for JMeter

**Solution**:
```bash
# Increase heap size
export HEAP="-Xms2g -Xmx4g"

# Or edit jmeter.bat / jmeter.sh
HEAP="-Xms2g -Xmx4g -XX:MaxMetaspaceSize=512m"

# Run with custom heap
jmeter -n -t test.jmx -JXmx4g -JXms2g
```

#### 4. "Session not found" errors in tests

**Problem**: Session timeout or cookie not maintained

**Solution**:
```bash
# Increase session timeout in web.xml
<session-config>
    <session-timeout>60</session-timeout>  <!-- 60 minutes -->
</session-config>

# In JMeter, ensure HTTP Cookie Manager is added
# In Postman, check "Automatically follow redirects"
```

#### 5. Slow test execution

**Problem**: Network latency or server overload

**Solution**:
```bash
# Check server load
top
htop

# Check network latency
ping localhost

# Reduce concurrent users
# Edit JMeter test plan: reduce thread count

# Add think time between requests
# In JMeter: Add "Constant Timer" or "Uniform Random Timer"
```

#### 6. Database deadlocks

**Problem**: Concurrent transactions causing deadlocks

**Solution**:
```sql
-- Check for deadlocks
SHOW ENGINE INNODB STATUS;

-- Review transaction isolation level
SELECT @@transaction_isolation;

-- Consider using READ-COMMITTED
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
```

### Log Locations

```
Application Logs:
├── Tomcat Access Log: $CATALINA_HOME/logs/localhost_access_log.txt
├── Tomcat Error Log: $CATALINA_HOME/logs/catalina.out
└── Application Log: logs/syos-app.log (if configured)

Test Logs:
├── JMeter Log: testing/jmeter/jmeter.log
├── JMeter Results: testing/jmeter/results/results.jtl
└── Newman Log: console output or --reporters cli

Database Logs:
├── MySQL Error Log: /var/log/mysql/error.log
├── MySQL Slow Query: /var/log/mysql/slow-query.log
└── MySQL General Log: /var/log/mysql/general.log
```

### Getting Help

1. **Check Documentation**
   - This README.md
   - PDF reference: CB011366 (1).pdf
   - JMeter docs: https://jmeter.apache.org/usermanual/
   - Postman docs: https://learning.postman.com/

2. **Review Logs**
   - Always check logs first
   - Look for stack traces and error messages

3. **Test in Isolation**
   - Test individual endpoints first
   - Gradually increase load
   - Identify which component is failing

4. **Monitor Resources**
   - CPU, Memory, Disk, Network
   - Database connections
   - Thread pool usage

---

## 📝 Additional Resources

### JMeter Best Practices

1. **Always use CLI mode for serious testing** - GUI mode is for development only
2. **Use CSV Data Set Config** for realistic test data
3. **Add timers** to simulate real user behavior
4. **Monitor server resources** during tests
5. **Run tests from separate machine** for accurate results

### Postman/Newman Best Practices

1. **Use environment variables** for different environments
2. **Chain requests** using variables (save IDs from responses)
3. **Use pre-request scripts** for dynamic data
4. **Add negative tests** (invalid inputs, unauthorized access)
5. **Organize tests** in folders by feature

### Performance Testing Tips

1. **Baseline First** - Test with 1 user to establish baseline
2. **Ramp Up Gradually** - Don't start with max load
3. **Monitor Everything** - CPU, memory, disk, network, database
4. **Test Realistic Scenarios** - Mix of operations, not just one endpoint
5. **Document Results** - Keep records of all test runs

### Database Optimization

1. **Index Critical Columns** - username, foreign keys, date fields
2. **Optimize Queries** - Use EXPLAIN to analyze
3. **Connection Pooling** - Reuse connections
4. **Batch Operations** - Insert/update multiple rows at once
5. **Archive Old Data** - Keep tables lean

---

## 🎯 Success Criteria

Your testing is successful if:

✅ **Unit Tests**
- All tests pass
- Code coverage > 75%
- No critical bugs

✅ **API Tests**
- All endpoints return expected responses
- Authentication/authorization works correctly
- Data validation functions properly

✅ **Performance Tests**
- System handles 200 concurrent users
- Average response time < 2 seconds
- Error rate < 1%
- No memory leaks
- No database deadlocks

✅ **Load Tests**
- System gracefully handles overload
- Request queue prevents connection refused
- Thread pool properly utilized
- Database connection pool stable

---

## 📞 Support

For issues or questions:

1. Check this README.md thoroughly
2. Review the PDF reference document (CB011366 (1).pdf)
3. Check application logs
4. Test with reduced load first
5. Verify database and Tomcat configuration

---

**Testing prepared for: SYOS POS System**
**Version: 1.0**
**Date: 2025-11-07**
**Environment: Development/Testing**

