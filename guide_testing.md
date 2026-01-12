# SYOS POS System - Complete Testing Guide

## Table of Contents
- [Overview](#overview)
- [Test Resources](#test-resources)
- [Prerequisites](#prerequisites)
- [Unit Testing with JUnit & Mockito](#unit-testing-with-junit--mockito)
- [API Testing with Postman](#api-testing-with-postman)
- [Load Testing with JMeter](#load-testing-with-jmeter)
- [Manual Testing](#manual-testing)
- [Test Data](#test-data)
- [Troubleshooting](#troubleshooting)

---

## Overview

This guide provides comprehensive instructions for testing the SYOS POS System across all layers:
- **Unit Tests** - JUnit 5 and Mockito for servlet testing
- **API Tests** - Postman collection for endpoint testing
- **Load Tests** - JMeter for concurrent user testing (200 users)
- **Manual Tests** - Web application testing scenarios

---

## Test Resources

All testing resources are located in the `testing/` directory:

```
testing/
├── jmeter/
│   └── pos-system-load-test.jmx           # JMeter test plan (200 concurrent users)
├── postman/
│   └── SYOS-POS-API-Tests.json            # Postman collection (19 requests)
└── junit/
    └── pos-web/src/test/java/com/syos/web/controllers/
        ├── LoginServletTest.java           # Login tests (10 tests)
        ├── DashboardServletTest.java       # Dashboard tests (7 tests)
        ├── SalesServletTest.java           # Sales tests (19 tests)
        ├── InventoryServletTest.java       # Inventory tests (17 tests)
        ├── ReportsServletTest.java         # Reports tests (16 tests)
        ├── LogoutServletTest.java          # Logout tests (16 tests)
        └── MockServletConfig.java          # Test helper
```

**Total Test Coverage:**
- **85+ JUnit test methods**
- **19 Postman API requests**
- **7 JMeter test scenarios**
- **200 concurrent user simulation**

---

## Prerequisites

### 1. For Unit Testing (JUnit/Mockito)
- Java JDK 17+
- Maven 3.8+
- Dependencies already configured in `pom.xml`

### 2. For API Testing (Postman)
- Postman Desktop Application or Postman CLI
- Download: https://www.postman.com/downloads/
- Running POS Web Application (http://localhost:8080/pos-web/)

### 3. For Load Testing (JMeter)
- Apache JMeter 5.x
- Download: https://jmeter.apache.org/download_jmeter.cgi
- Running POS Web Application
- MySQL database running

### 4. For All Tests
- MySQL database created and seeded:
  ```bash
  mysql -u root -p < sql/create_database.sql
  ```
- Application built successfully:
  ```bash
  mvn clean install
  ```

---

## Unit Testing with JUnit & Mockito

### Running All Unit Tests

```bash
# From project root
cd /home/user/latest-pos
mvn test
```

**Expected Output:**
```
[INFO] Results:
[INFO]
[INFO] Tests run: 85, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

### Running Tests for Specific Module

```bash
# Test only pos-core
cd pos-core && mvn test

# Test only pos-web (includes all servlet tests)
cd pos-web && mvn test
```

### Running Specific Test Class

```bash
# Run only LoginServlet tests
mvn test -Dtest=LoginServletTest

# Run only SalesServlet tests
mvn test -Dtest=SalesServletTest

# Run multiple test classes
mvn test -Dtest=LoginServletTest,DashboardServletTest
```

### Running Specific Test Method

```bash
# Run a single test method
mvn test -Dtest=LoginServletTest#testDoPost_WithValidCredentials_CreatesSessionAndRedirectsToDashboard

# Run with pattern matching
mvn test -Dtest=LoginServletTest#*ValidCredentials*
```

### Test Coverage by Servlet

| Servlet | Test Class | Test Methods | Coverage |
|---------|-----------|--------------|----------|
| LoginServlet | LoginServletTest | 10 tests | Login success/failure, session creation |
| DashboardServlet | DashboardServletTest | 7 tests | Metrics, alerts, data loading |
| SalesServlet | SalesServletTest | 19 tests | Sale creation, items, checkout |
| InventoryServlet | InventoryServletTest | 17 tests | Stock management, movements |
| ReportsServlet | ReportsServletTest | 16 tests | All report types |
| LogoutServlet | LogoutServletTest | 16 tests | Session cleanup, redirects |

### Generating Test Reports

```bash
# Generate Surefire test reports
mvn test

# View reports at:
# pos-web/target/surefire-reports/
```

---

## API Testing with Postman

### Step 1: Import Collection

1. Open Postman Desktop Application
2. Click **Import** button
3. Select file: `testing/postman/SYOS-POS-API-Tests.json`
4. Collection "SYOS POS API Tests" will be imported with 5 folders

### Step 2: Verify Environment Variables

The collection includes a variable:
- `baseUrl` = http://localhost:8080/pos-web

To modify:
1. Click on the collection name
2. Go to **Variables** tab
3. Update `baseUrl` if your application runs on a different port/path

### Step 3: Start the Application

```bash
# Deploy to Tomcat
cp pos-web/target/pos-web.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh

# Or use Maven plugin
cd pos-web
mvn tomcat9:run
```

### Step 4: Run Tests

#### Option A: Run Entire Collection

1. Click on collection name "SYOS POS API Tests"
2. Click **Run** button
3. Select all folders
4. Click **Run SYOS POS API Tests**

#### Option B: Run Individual Folders

1. Expand collection
2. Select a folder (e.g., "Sales Management")
3. Click **Run** on the folder
4. View results

#### Option C: Run Individual Requests

1. Click on any request (e.g., "Login")
2. Click **Send**
3. View response in the lower panel

### Step 5: View Test Results

Each request includes test scripts that validate:
- ✅ Status code (200, 201, or 302)
- ✅ Response time (< 500ms)
- ✅ Content-Type headers
- ✅ Response body structure

**Test Results Panel** shows:
- PASSED tests in green
- FAILED tests in red
- Test execution time

### API Endpoints Tested

**Authentication (2 requests)**
- POST /login - Login with credentials
- GET /logout - User logout

**Sales Management (5 requests)**
- GET /sales/new - Create new sale
- POST /sales/add-item - Add items
- POST /sales/complete - Complete sale
- GET /sales/list - List all sales
- GET /sales/view/1 - View sale details

**Inventory Management (6 requests)**
- GET /inventory - List inventory
- POST /inventory/add - Add stock
- POST /inventory/move-to-shelf - Move items
- GET /inventory/low-stock - Low stock alert
- GET /inventory/expiring - Expiring items

**Reports (4 requests)**
- GET /reports/daily-sales - Daily sales report
- GET /reports/stock - Stock report
- GET /reports/reorder - Reorder report

**User Management (2 requests)**
- GET /users - List users
- POST /users/register - Register user

---

## Load Testing with JMeter

### Step 1: Install JMeter

```bash
# Download JMeter 5.x from https://jmeter.apache.org/download_jmeter.cgi
# Extract to a directory, e.g., /opt/apache-jmeter-5.6

# Verify installation
/opt/apache-jmeter-5.6/bin/jmeter --version
```

### Step 2: Start the Application

```bash
# Ensure application is running
cp pos-web/target/pos-web.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh

# Verify application is accessible
curl http://localhost:8080/pos-web/
```

### Step 3: Run Load Test

#### Option A: GUI Mode (for Test Development)

```bash
# Open JMeter GUI
/opt/apache-jmeter-5.6/bin/jmeter

# In GUI:
# 1. File → Open → testing/jmeter/pos-system-load-test.jmx
# 2. Review test plan
# 3. Click green "Start" button (play icon)
# 4. View results in listeners
```

#### Option B: CLI Mode (for Production Load Testing)

```bash
# Run from command line (recommended for load tests)
cd /home/user/latest-pos

/opt/apache-jmeter-5.6/bin/jmeter \
  -n \
  -t testing/jmeter/pos-system-load-test.jmx \
  -l testing/jmeter/results.jtl \
  -j testing/jmeter/jmeter.log \
  -e \
  -o testing/jmeter/html-report
```

**Parameters:**
- `-n` : Non-GUI mode
- `-t` : Test plan file
- `-l` : Results file (JTL format)
- `-j` : JMeter log file
- `-e` : Generate HTML report
- `-o` : Output folder for HTML report

### Step 4: View Results

#### During Test Execution

Watch the console output:
```
Creating summariser <summary>
Created the tree successfully using testing/jmeter/pos-system-load-test.jmx
Starting standalone test @ Sun Jan 12 14:30:00 UTC 2026 (1736692200000)
Waiting for possible Shutdown/StopTestNow/HeapDump/ThreadDump message on port 4445
summary +    100 in   5.2s =   19.2/s Avg:  1045 Min:   150 Max:  2500 Err:     0 (0.00%)
summary +    200 in  10.1s =   19.8/s Avg:  1012 Min:   145 Max:  2450 Err:     0 (0.00%)
```

#### After Test Completion

1. **HTML Report** (if generated with `-e -o`):
   ```bash
   # Open in browser
   firefox testing/jmeter/html-report/index.html
   ```

2. **JTL Results File**:
   ```bash
   # View results
   cat testing/jmeter/results.jtl | head -20
   ```

3. **JMeter Log**:
   ```bash
   # Check for errors
   tail -100 testing/jmeter/jmeter.log
   ```

### Load Test Configuration

The test plan simulates:
- **200 concurrent users**
- **60-second ramp-up** (gradual user increase)
- **5 iterations** per user
- **Total requests**: ~7,000 (200 users × 5 iterations × 7 requests)

**Test Scenarios:**
1. Login (POST /login)
2. Dashboard (GET /dashboard)
3. Create Sale (GET /sales/new)
4. Add Item to Sale (POST /sales/add-item)
5. View Inventory (GET /inventory)
6. Daily Sales Report (GET /reports/daily-sales)
7. Logout (GET /logout)

### Performance Metrics to Monitor

- **Response Time**: Average should be < 500ms
- **Throughput**: Requests per second
- **Error Rate**: Should be < 1%
- **Latency**: Time to first byte
- **Active Threads**: Concurrent users

### Expected Results

For a properly configured system:
- ✅ **Average Response Time**: 200-500ms
- ✅ **95th Percentile**: < 1000ms
- ✅ **Error Rate**: 0%
- ✅ **Throughput**: 50-100 requests/sec

---

## Manual Testing

### Test Scenario 1: User Login

1. Navigate to `http://localhost:8080/pos-web/`
2. Enter credentials:
   - Username: `admin`
   - Password: `admin123`
3. Click **Login**
4. **Expected**: Redirect to dashboard with welcome message

**Test Variations:**
- ✅ Valid credentials for all roles (admin, manager_new, cashier_new, customer_new)
- ✅ Invalid username
- ✅ Invalid password
- ✅ Empty fields

### Test Scenario 2: Create a Sale

1. Login as `cashier_new` / `123456`
2. Navigate to **Sales** → **Create New Sale**
3. Add items:
   - Item Code: `MILK001`, Quantity: `2`
   - Item Code: `BREAD001`, Quantity: `1`
4. Click **Add Item** for each
5. Enter Cash Tendered: `20.00`
6. Click **Complete Sale**
7. **Expected**: Bill generated, change calculated, inventory updated

**Verify:**
- ✅ Total amount calculated correctly
- ✅ Change amount displayed
- ✅ Bill number generated
- ✅ Inventory quantities decreased

### Test Scenario 3: Add Inventory

1. Login as `manager_new` / `123456`
2. Navigate to **Inventory** → **Add Stock**
3. Enter item details:
   - Code: `TEST001`
   - Name: `Test Item`
   - Price: `5.00`
   - Quantity: `100`
   - Expiry Date: `2026-12-31`
4. Click **Add Stock**
5. **Expected**: Item added, appears in inventory list

**Verify:**
- ✅ Item appears in inventory list
- ✅ State is "IN_STORE"
- ✅ Can move to shelf
- ✅ Quantity is correct

### Test Scenario 4: Generate Reports

1. Login as `admin` / `admin123`
2. Navigate to **Reports**
3. Select **Daily Sales Report**
4. Enter date: `2026-01-12` (today)
5. Click **Generate**
6. **Expected**: Report displays sales for the date

**Test Other Reports:**
- ✅ Stock Report (current inventory)
- ✅ Reorder Report (low stock items)
- ✅ Export reports to file

### Test Scenario 5: Concurrent Users

1. **Open 3 browser tabs** (or use incognito/different browsers)
2. **Login in each tab**:
   - Tab 1: `admin` / `admin123`
   - Tab 2: `manager_new` / `123456`
   - Tab 3: `cashier_new` / `123456`
3. **Perform operations simultaneously**:
   - Tab 1: View reports
   - Tab 2: Add inventory
   - Tab 3: Create sale
4. **Expected**: All operations succeed without conflicts

**Verify:**
- ✅ No session conflicts
- ✅ Data integrity maintained
- ✅ No race conditions in inventory
- ✅ Each user sees correct role-based menus

---

## Test Data

### Default User Credentials

| Username | Password | Role | Access Level |
|----------|----------|------|--------------|
| admin | admin123 | ADMIN | Full access |
| manager_new | 123456 | MANAGER | Sales, Inventory, Reports |
| cashier_new | 123456 | CASHIER | Sales only |
| customer_new | 123456 | CUSTOMER | View only |

### Sample Inventory Items

| Item Code | Name | Price | Quantity | State |
|-----------|------|-------|----------|-------|
| MILK001 | Fresh Milk 1L | $3.50 | 100 | IN_STORE |
| BREAD001 | White Bread | $2.50 | 150 | ON_SHELF |
| RICE001 | Basmati Rice 5kg | $15.00 | 200 | IN_STORE |
| EGGS001 | Eggs (Dozen) | $4.50 | 80 | ON_SHELF |

### Test Sale Data

For manual testing, use these combinations:
```
Sale 1:
- MILK001 × 2 = $7.00
- BREAD001 × 1 = $2.50
- Total: $9.50

Sale 2:
- RICE001 × 1 = $15.00
- EGGS001 × 2 = $9.00
- Total: $24.00
```

---

## Troubleshooting

### JUnit Tests Failing

**Problem**: Tests fail with `ClassNotFoundException`
```bash
# Solution: Rebuild all modules
cd /home/user/latest-pos
mvn clean install
```

**Problem**: Tests fail with database connection errors
```bash
# Solution: Verify MySQL is running and database exists
sudo systemctl status mysql
mysql -u root -p -e "SHOW DATABASES LIKE 'syos_db';"
```

**Problem**: Mock objects not working
```bash
# Solution: Check Mockito version in pom.xml
# Should be mockito-core:5.7.0 and mockito-junit-jupiter:5.7.0
mvn dependency:tree | grep mockito
```

### Postman Tests Failing

**Problem**: Connection refused
```bash
# Solution: Verify application is running
curl http://localhost:8080/pos-web/
# Should return HTML or redirect
```

**Problem**: Authentication failing
```
# Solution: Clear Postman cookies
# Postman → Cookies → Remove all cookies for localhost
```

**Problem**: 404 errors
```
# Solution: Check baseUrl variable
# Should be: http://localhost:8080/pos-web
# NOT: http://localhost:8080/pos-web/ (no trailing slash)
```

### JMeter Load Test Issues

**Problem**: OutOfMemoryError
```bash
# Solution: Increase JMeter heap size
export JVM_ARGS="-Xms1g -Xmx4g"
/opt/apache-jmeter-5.6/bin/jmeter -n -t ...
```

**Problem**: Connection timeouts
```bash
# Solution: Increase Tomcat thread pool
# Edit $CATALINA_HOME/conf/server.xml
# Set maxThreads="500" minSpareThreads="50"
```

**Problem**: Database connection pool exhausted
```bash
# Solution: Increase connection pool size
# Edit pos-core/src/main/resources/config/application.properties
# Set db.pool.max=50
```

**Problem**: High error rate
```bash
# Solution: Check application logs
tail -f $CATALINA_HOME/logs/catalina.out

# Check for:
# - Database deadlocks
# - Thread safety issues
# - Memory leaks
```

### Manual Testing Issues

**Problem**: Login not working
```
# 1. Check database has users
mysql -u root -p -e "USE syos_db; SELECT username, role FROM users;"

# 2. Check password hash
# admin123 should hash to: 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9

# 3. Clear browser cookies
# Chrome: Settings → Privacy → Clear browsing data
```

**Problem**: Session timeout too fast
```
# Solution: Increase session timeout
# Edit pos-web/src/main/webapp/WEB-INF/web.xml
# <session-timeout>30</session-timeout> (30 minutes)
```

**Problem**: Inventory quantities not updating
```
# Solution: Check transaction isolation
# Verify synchronized blocks in services
# Check database transaction logs
```

---

## Test Execution Checklist

Before submitting/deploying:

- [ ] All JUnit tests pass (`mvn test`)
- [ ] All Postman requests return 2xx or 302 status
- [ ] JMeter load test completes with < 1% error rate
- [ ] Manual login works for all user roles
- [ ] Sales creation updates inventory correctly
- [ ] Reports generate without errors
- [ ] Concurrent user test shows no conflicts
- [ ] Application logs show no errors
- [ ] Database integrity maintained

---

## Performance Benchmarks

Expected performance on a standard development machine:

| Test Type | Metric | Target | Acceptable |
|-----------|--------|--------|------------|
| Unit Tests | Execution Time | < 10s | < 30s |
| Postman | Response Time | < 200ms | < 500ms |
| JMeter (200 users) | Avg Response | < 300ms | < 1000ms |
| JMeter (200 users) | Throughput | > 50 req/s | > 30 req/s |
| JMeter (200 users) | Error Rate | 0% | < 1% |

---

## Continuous Integration

For automated testing in CI/CD pipelines:

```bash
# Run all tests
mvn clean test

# Run with coverage report
mvn clean verify

# Skip tests during build (not recommended)
mvn clean install -DskipTests

# Run only fast tests
mvn test -Dtest=*Test

# Fail build on test failures
mvn test -Dmaven.test.failure.ignore=false
```

---

## Additional Resources

- **JUnit 5 Documentation**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **Postman Learning**: https://learning.postman.com/
- **JMeter User Manual**: https://jmeter.apache.org/usermanual/index.html

---

## Support

For testing issues:
1. Check application logs: `$CATALINA_HOME/logs/catalina.out`
2. Check test logs: `pos-web/target/surefire-reports/`
3. Verify all prerequisites are installed
4. Ensure database is properly seeded

---

**Testing is complete when all tests pass and the system behaves correctly under concurrent load!**

---

*Last Updated: 2026-01-12*
*SYOS POS System Testing Guide v1.0*
