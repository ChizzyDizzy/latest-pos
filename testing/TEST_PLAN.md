# Complete Test Plan for SYOS POS System

## Overview

This document provides comprehensive testing instructions for the SYOS POS System covering all 8 servlets with three types of testing:
- **Unit Testing** (JUnit + Mockito)
- **API Testing** (Postman)
- **Load Testing** (JMeter - 200 concurrent users)

**Base URL**: `http://localhost:8080/pos-web/`

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Servlet Coverage](#servlet-coverage)
3. [Unit Testing with JUnit](#unit-testing-with-junit)
4. [API Testing with Postman](#api-testing-with-postman)
5. [Load Testing with JMeter](#load-testing-with-jmeter)
6. [Test Results and Reports](#test-results-and-reports)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

1. **Java 17** - JDK installed and configured
2. **Maven 3.8+** - Build automation tool
3. **Apache Tomcat 9** - Application server
4. **MySQL 8.0+** - Database server
5. **Postman** - API testing tool
6. **Apache JMeter 5.6.3** - Load testing tool

### System Setup

Before running any tests, ensure:

1. **Database is running** and populated with test data
2. **Application is deployed** to Tomcat at `http://localhost:8080/pos-web/`
3. **Test user exists** with credentials:
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`

### Verify Application is Running

```bash
# Check if the application is accessible
curl -I http://localhost:8080/pos-web/login

# Expected output: HTTP/1.1 200 OK
```

---

## Servlet Coverage

### All 8 Servlets Covered by Tests

| # | Servlet | Endpoint | JUnit Tests | Postman | JMeter | Priority |
|---|---------|----------|-------------|---------|---------|----------|
| 1 | **LoginServlet** | `/login` | ✅ 10 tests | ✅ 3 requests | ✅ All groups | Critical |
| 2 | **LogoutServlet** | `/logout` | ✅ 16 tests | ✅ 1 request | ✅ All groups | Critical |
| 3 | **DashboardServlet** | `/dashboard` | ✅ 7 tests | ✅ 1 request | ✅ All groups | High |
| 4 | **SalesServlet** | `/sales/*` | ✅ 19 tests | ✅ 8 requests | ✅ 80 users | Critical |
| 5 | **InventoryServlet** | `/inventory/*` | ✅ 17 tests | ✅ 6 requests | ✅ 60 users | High |
| 6 | **ReportsServlet** | `/reports/*` | ✅ 16 tests | ✅ 5 requests | ✅ 40 users | Medium |
| 7 | **ProductsServlet** | `/products` | ✅ 8 tests | ✅ 1 request | ✅ 40 users | Medium |
| 8 | **UsersServlet** | `/users/*` | ✅ 16 tests | ✅ 3 requests | ✅ 20 users | High |

**Total Coverage**:
- **109 JUnit test methods**
- **28 Postman API requests**
- **200 concurrent users** in JMeter
- **~1,800+ total requests** in load test (200 users × 3 loops × ~3 requests avg)

---

## Unit Testing with JUnit

### Test Framework

- **JUnit 5.10.0** (Jupiter)
- **Mockito 5.7.0** (Mocking framework)
- **Maven Surefire Plugin** (Test runner)

### Test File Locations

```
pos-web/src/test/java/com/syos/web/controllers/
├── LoginServletTest.java          (10 tests)
├── LogoutServletTest.java         (16 tests)
├── DashboardServletTest.java      (7 tests)
├── SalesServletTest.java          (19 tests)
├── InventoryServletTest.java      (17 tests)
├── ReportsServletTest.java        (16 tests)
├── ProductsServletTest.java       (8 tests)  ← NEW
├── UsersServletTest.java          (16 tests) ← NEW
└── MockServletConfig.java         (Helper class)
```

### Running Unit Tests

#### Run All Tests

```bash
# From project root
mvn clean test

# From pos-web module
cd pos-web && mvn test
```

#### Run Specific Test Class

```bash
# Run only SalesServlet tests
mvn test -Dtest=SalesServletTest

# Run only the new ProductsServlet tests
mvn test -Dtest=ProductsServletTest

# Run only the new UsersServlet tests
mvn test -Dtest=UsersServletTest
```

#### Run Specific Test Method

```bash
# Run a single test method
mvn test -Dtest=LoginServletTest#testDoPost_WithValidCredentials_CreatesSessionAndRedirectsToDashboard
```

#### Run Multiple Test Classes

```bash
# Run multiple test classes
mvn test -Dtest=LoginServletTest,SalesServletTest,ProductsServletTest
```

### Expected Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.syos.web.controllers.ProductsServletTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.543 s
[INFO] Running com.syos.web.controllers.UsersServletTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.612 s
...
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 109, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

### Test Coverage by Servlet

#### ProductsServlet Tests (8 tests)

```bash
mvn test -Dtest=ProductsServletTest
```

- ✅ Valid session displays available products
- ✅ Without session redirects to login
- ✅ Without username redirects to login
- ✅ Filters out IN_STORE items (only ON_SHELF shown)
- ✅ Filters out expired items
- ✅ Filters out zero quantity items
- ✅ Empty inventory displays empty list
- ✅ Service exception forwards to error page

#### UsersServlet Tests (16 tests)

```bash
mvn test -Dtest=UsersServletTest
```

- ✅ List users as admin displays users list
- ✅ List users with null path info
- ✅ Register form as admin shows form
- ✅ Non-admin returns 403 forbidden
- ✅ Without session returns 403
- ✅ Invalid path returns 404
- ✅ Service exception forwards to error page
- ✅ Register user as admin creates user
- ✅ Register with ADMIN role
- ✅ POST as non-admin returns 403
- ✅ POST without session returns 403
- ✅ POST with invalid path returns 400
- ✅ POST with null path returns 400
- ✅ Service exception forwards with error
- ✅ Invalid role forwards with error

---

## API Testing with Postman

### Postman Collection

**File**: `testing/postman/Complete-POS-API-Tests.json`

### Importing the Collection

1. Open Postman
2. Click **Import** button
3. Select `testing/postman/Complete-POS-API-Tests.json`
4. Collection will appear in your workspace

### Collection Structure

```
Complete POS System API Tests/
├── 1. Authentication/
│   ├── Login - Valid Credentials
│   ├── Login - Invalid Credentials
│   └── Logout
├── 2. Dashboard/
│   └── Get Dashboard
├── 3. Sales/
│   ├── New Sale Form
│   ├── Get Available Items (JSON API)
│   ├── Add Item to Sale
│   ├── Remove Item from Sale
│   ├── Complete Sale
│   ├── List Today's Bills
│   ├── View Bill
│   └── Clear Sale
├── 4. Inventory/
│   ├── List All Inventory
│   ├── Add Stock Form
│   ├── Add New Stock
│   ├── Move to Shelf (JSON API)
│   ├── Low Stock Items
│   └── Expiring Items
├── 5. Reports/
│   ├── Reports Menu
│   ├── Daily Sales Report
│   ├── Daily Sales Report - Specific Date
│   ├── Stock Report
│   └── Reorder Report
├── 6. Products/
│   └── View Products Catalog
└── 7. User Management/
    ├── List Users (Admin Only)
    ├── User Registration Form (Admin Only)
    └── Register New User (Admin Only)
```

**Total**: 28 API requests with test scripts

### Environment Variables

The collection uses a variable for the base URL:

- **Variable**: `baseUrl`
- **Value**: `http://localhost:8080/pos-web`

You can override this in Postman environments if testing different deployments.

### Running Postman Tests

#### Run Entire Collection

1. Right-click on **Complete POS System API Tests** collection
2. Select **Run collection**
3. Click **Run Complete POS System API Tests**
4. View results

#### Run Specific Folder

1. Right-click on a folder (e.g., **3. Sales**)
2. Select **Run folder**
3. View results

#### Expected Test Results

Each request includes test scripts that verify:
- ✅ Status code is correct (200, 302, 403, etc.)
- ✅ Response time is acceptable (<500ms for most, <1500ms for reports)
- ✅ Content-Type headers are correct
- ✅ Response body contains expected content
- ✅ JSON structure is valid (for API endpoints)
- ✅ Session cookies are set properly

### Key API Endpoints

#### JSON API Endpoints (AJAX)

```bash
# Get available items for sale (returns JSON array)
GET /sales/available-items

# Add item to sale (returns JSON response)
POST /sales/add-item
Body: itemCode=ITEM-001&quantity=2

# Remove item from sale (returns JSON response)
POST /sales/remove-item
Body: itemCode=ITEM-001

# Move items to shelf (returns JSON response)
POST /inventory/move-to-shelf
Body: itemCode=ITEM-001&quantity=10
```

#### Form-Based Endpoints

```bash
# Login
POST /login
Body: username=admin&password=admin123

# Add inventory
POST /inventory/add
Body: code=TEST-001&name=Test&price=10.00&quantity=100&expiryDate=2026-12-31

# Complete sale
POST /sales/complete
Body: cashTendered=100.00

# Register user (Admin only)
POST /users/register
Body: username=newuser&email=user@example.com&password=pass&role=CASHIER
```

### Testing Different User Roles

To test role-based access control:

1. **Login as Admin** first
2. **Test Admin-only endpoints** (`/users/*`)
3. **Logout**
4. **Login as Cashier** (if available)
5. **Verify 403 Forbidden** on `/users/*` endpoints

---

## Load Testing with JMeter

### JMeter Test Plan

**File**: `testing/jmeter/Complete-POS-Load-Test-200-Users.jmx`

### Test Configuration

- **Total Users**: 200 concurrent users
- **Ramp-up Time**: 60 seconds (users start gradually)
- **Loops**: 3 iterations per user
- **Base URL**: `http://localhost:8080/pos-web/`
- **Total Requests**: ~1,800+ requests
- **Test Duration**: ~5-10 minutes

### User Distribution

The test simulates realistic usage patterns:

| Thread Group | Users | % of Load | Focus Area |
|--------------|-------|-----------|------------|
| **Sales Operations** | 80 | 40% | Sales workflow (critical path) |
| **Inventory Operations** | 60 | 30% | Inventory management |
| **Reports & Products** | 40 | 20% | Reports and product catalog |
| **User Management** | 20 | 10% | Admin user operations |

### Test Scenarios

#### Sales Operations (80 users)
1. Login
2. View Dashboard
3. New Sale Form
4. Get Available Items (JSON)
5. Add Item to Sale
6. Complete Sale
7. List Today's Sales
8. Logout

#### Inventory Operations (60 users)
1. Login
2. View All Inventory
3. Low Stock Items
4. Expiring Items
5. Logout

#### Reports & Products (40 users)
1. Login
2. View Products Catalog
3. Daily Sales Report
4. Stock Report
5. Reorder Report
6. Logout

#### User Management (20 users)
1. Login as Admin
2. List Users
3. User Registration Form
4. Logout

### Running JMeter Tests

#### Prerequisites

1. **Install JMeter 5.6.3**
   ```bash
   # Download from https://jmeter.apache.org/download_jmeter.cgi
   # Extract and add to PATH
   ```

2. **Ensure application is running** at `http://localhost:8080/pos-web/`

3. **Create results directory**
   ```bash
   cd testing/jmeter
   mkdir -p results
   ```

#### Run in GUI Mode (for debugging)

```bash
# Navigate to JMeter installation
cd /path/to/jmeter/bin

# Run JMeter GUI
./jmeter

# Then: File → Open → Select Complete-POS-Load-Test-200-Users.jmx
# Click the green "Start" button to run
```

#### Run in CLI Mode (recommended for actual testing)

```bash
# From jmeter/bin directory
./jmeter -n -t /home/user/latest-pos/testing/jmeter/Complete-POS-Load-Test-200-Users.jmx \
  -l /home/user/latest-pos/testing/jmeter/results/test-results.jtl \
  -e -o /home/user/latest-pos/testing/jmeter/results/html-report

# Explanation:
# -n : Run in non-GUI mode
# -t : Test plan file
# -l : Results log file
# -e : Generate HTML report
# -o : Output folder for HTML report
```

#### View Results

After test completes:

```bash
# Open HTML report in browser
open testing/jmeter/results/html-report/index.html

# Or view raw results
cat testing/jmeter/results/test-results.jtl
```

### Performance Metrics to Monitor

#### Response Times

Expected response times under load:

| Endpoint Type | Target | Acceptable | Critical |
|---------------|--------|------------|----------|
| Login/Logout | <300ms | <500ms | >1000ms |
| Dashboard | <500ms | <1000ms | >2000ms |
| Sales Operations | <400ms | <800ms | >1500ms |
| Inventory List | <600ms | <1000ms | >2000ms |
| Reports | <800ms | <1500ms | >3000ms |
| JSON APIs | <200ms | <400ms | >800ms |

#### Throughput

- **Target**: >30 requests/second
- **Acceptable**: >20 requests/second
- **Critical**: <10 requests/second

#### Error Rate

- **Target**: 0% errors
- **Acceptable**: <1% errors
- **Critical**: >5% errors

#### Server Resources

Monitor during test:
- CPU usage should stay <80%
- Memory usage should stay <70%
- Database connections should not pool out
- Thread pool (Tomcat maxThreads=200) should not saturate

### Customizing the Test

You can adjust test parameters by editing variables in JMeter:

```xml
<elementProp name="NUM_USERS">
  <stringProp name="Argument.value">200</stringProp>  <!-- Change user count -->
</elementProp>
<elementProp name="RAMP_UP_TIME">
  <stringProp name="Argument.value">60</stringProp>   <!-- Change ramp-up -->
</elementProp>
<elementProp name="LOOPS">
  <stringProp name="Argument.value">3</stringProp>    <!-- Change iterations -->
</elementProp>
```

Or override via command line:

```bash
./jmeter -n -t Complete-POS-Load-Test-200-Users.jmx \
  -JNUM_USERS=100 \
  -JRAMP_UP_TIME=30 \
  -JLOOPS=5 \
  -l results/test-100users.jtl
```

---

## Test Results and Reports

### JUnit Test Reports

After running `mvn test`, reports are generated:

```
pos-web/target/surefire-reports/
├── TEST-com.syos.web.controllers.ProductsServletTest.xml
├── TEST-com.syos.web.controllers.UsersServletTest.xml
├── ... (other test reports)
└── index.html  (if configured)
```

View summary:
```bash
cat pos-web/target/surefire-reports/*.xml | grep "testcase"
```

### Postman Test Reports

Export results from Postman:

1. After running collection, click **Export Results**
2. Save as JSON or HTML
3. Share with team

Or use Newman (CLI runner):

```bash
# Install Newman
npm install -g newman

# Run collection
newman run testing/postman/Complete-POS-API-Tests.json \
  --environment your-environment.json \
  --reporters cli,html \
  --reporter-html-export results/postman-report.html
```

### JMeter Test Reports

JMeter generates comprehensive HTML reports with:

- **Dashboard**: Overview statistics
- **Charts**: Response times, throughput, errors
- **Statistics**: Min/Max/Avg/Percentiles
- **Graphs**: Response time over time, hits per second

Access at: `testing/jmeter/results/html-report/index.html`

---

## Troubleshooting

### Common Issues

#### 1. Unit Tests Fail - "ServiceFactory is null"

**Problem**: Mock servlet context not properly configured

**Solution**:
```java
when(servletContext.getAttribute("serviceFactory")).thenReturn(serviceFactory);
```

#### 2. Postman - "Connection Refused"

**Problem**: Application not running

**Solution**:
```bash
# Verify app is running
curl http://localhost:8080/pos-web/login

# If not, deploy to Tomcat
cd pos-web
mvn clean package
# Copy pos-web.war to Tomcat webapps/
```

#### 3. Postman - "401 Unauthorized" or Session Issues

**Problem**: Session cookies not being maintained

**Solution**:
- Ensure "Login - Valid Credentials" runs first
- Check that Postman is configured to automatically handle cookies
- Settings → General → Enable "Automatically follow redirects"

#### 4. JMeter - "Connection Timeout"

**Problem**: Application can't handle 200 concurrent users

**Solution**:
```bash
# Check Tomcat configuration (conf/server.xml)
<Connector port="8080"
           maxThreads="200"      <!-- Ensure this is 200+ -->
           minSpareThreads="25"
           maxConnections="200"   <!-- Increase if needed -->
           acceptCount="100" />
```

#### 5. JMeter - High Error Rate

**Problem**: Application errors under load

**Solution**:
- Check application logs: `tail -f tomcat/logs/catalina.out`
- Reduce concurrent users to find breaking point
- Check database connection pool size
- Monitor server resources (CPU, memory)

#### 6. Database Connection Pool Exhausted

**Problem**: "Cannot get connection from pool"

**Solution**:
```java
// In DatabaseConnectionPool.java
// Increase pool size
private static final int MAX_POOL_SIZE = 50;  // Increase from default
```

#### 7. JUnit Tests Run Slowly

**Problem**: Tests taking too long

**Solution**:
```bash
# Run tests in parallel
mvn test -T 4  # Use 4 threads
```

#### 8. Application Returns 404 for all endpoints

**Problem**: Wrong context path

**Solution**:
- Verify deployment context: Should be `/pos-web` not `/pos`
- Check `pos-web/pom.xml` for `<finalName>pos-web</finalName>`
- Verify URL: `http://localhost:8080/pos-web/` (with trailing slash for base)

---

## Test Execution Checklist

### Pre-Test Checklist

- [ ] MySQL database is running
- [ ] Database is populated with test data (items, users, etc.)
- [ ] Application is deployed to Tomcat
- [ ] Application is accessible at `http://localhost:8080/pos-web/`
- [ ] Admin user exists (admin/admin123)
- [ ] Test data includes items with:
  - [ ] ON_SHELF status
  - [ ] IN_STORE status
  - [ ] Expired items
  - [ ] Low stock items

### Running All Tests

```bash
# Step 1: Run Unit Tests (fast - 30 seconds)
cd pos-web
mvn clean test

# Step 2: Run Postman Tests (medium - 2 minutes)
# Import and run collection in Postman
# Or use Newman:
newman run testing/postman/Complete-POS-API-Tests.json

# Step 3: Run JMeter Load Test (slow - 5-10 minutes)
cd testing/jmeter
mkdir -p results
jmeter -n -t Complete-POS-Load-Test-200-Users.jmx \
  -l results/test-results.jtl \
  -e -o results/html-report
```

### Post-Test Checklist

- [ ] All JUnit tests passed (109/109)
- [ ] All Postman tests passed (28/28)
- [ ] JMeter error rate <1%
- [ ] JMeter average response time <1000ms
- [ ] No application errors in logs
- [ ] Database connection pool stable
- [ ] Server resources within limits

---

## Summary

This test plan provides **comprehensive coverage** for all 8 servlets:

- **109 JUnit tests** for unit-level validation
- **28 Postman requests** for API integration testing
- **200 concurrent users** in JMeter for load testing
- **~1,800+ requests** in full load test execution

All tests are configured to work with the correct base URL: `http://localhost:8080/pos-web/`

### Test Coverage Summary

| Testing Type | Coverage | Status |
|--------------|----------|--------|
| **Unit Tests** | All 8 servlets, 109 test methods | ✅ Complete |
| **API Tests** | All 8 servlets, 28 API requests | ✅ Complete |
| **Load Tests** | All 8 servlets, 200 users, 4 groups | ✅ Complete |

### Next Steps

1. **Run all tests** to establish baseline
2. **Review results** and fix any failures
3. **Monitor performance** under load
4. **Tune application** based on results
5. **Re-run tests** to verify improvements
6. **Integrate into CI/CD** pipeline (optional)

---

**Document Version**: 1.0
**Last Updated**: 2026-01-12
**Author**: SYOS POS Development Team
