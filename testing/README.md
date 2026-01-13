# POS System Testing Guide

Complete testing suite for SYOS POS System with all 8 servlets covered.

**Application URL**: `http://localhost:8080/pos-web/`

---

## 📁 Test Files

```
testing/
├── postman/
│   └── Complete-POS-API-Tests.json          (28 API tests)
├── jmeter/
│   └── Complete-POS-Load-Test-200-Users.jmx (200 concurrent users)
└── README.md                                 (this file)

pos-web/src/test/java/com/syos/web/controllers/
├── LoginServletTest.java                     (10 tests)
├── LogoutServletTest.java                    (16 tests)
├── DashboardServletTest.java                 (7 tests)
├── SalesServletTest.java                     (19 tests)
├── InventoryServletTest.java                 (17 tests)
├── ReportsServletTest.java                   (16 tests)
├── ProductsServletTest.java                  (8 tests)
└── UsersServletTest.java                     (16 tests)
```

**Total**: 109 unit tests, 28 API tests, 200 concurrent users

---

## ✅ Before Testing

1. **Start your application**:
   ```bash
   # Deploy to Tomcat and start
   # Application should be running at: http://localhost:8080/pos-web/
   ```

2. **Verify it's working**:
   ```bash
   curl http://localhost:8080/pos-web/login
   # Should return HTML (200 OK)
   ```

3. **Ensure test user exists**:
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`

---

## 1️⃣ Unit Tests (JUnit + Mockito)

### Run All Unit Tests

```bash
cd pos-web
mvn clean test
```

### Run Specific Servlet Test

```bash
# Test a specific servlet
mvn test -Dtest=LoginServletTest
mvn test -Dtest=SalesServletTest
mvn test -Dtest=ProductsServletTest
```

### Expected Output

```
Tests run: 109, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 2️⃣ Postman API Tests

### Option A: Postman GUI (Easiest)

1. **Open Postman**
2. Click **Import** button
3. Select file: `testing/postman/Complete-POS-API-Tests.json`
4. Right-click the collection → **Run collection**
5. Click **Run**
6. View results

### Option B: Newman CLI

```bash
# Install Newman (first time only)
npm install -g newman

# Run tests
newman run testing/postman/Complete-POS-API-Tests.json
```

### What Gets Tested

- ✅ Login/Logout (3 tests)
- ✅ Dashboard (1 test)
- ✅ Sales operations (8 tests)
- ✅ Inventory management (6 tests)
- ✅ Reports (5 tests)
- ✅ Products catalog (1 test)
- ✅ User management (3 tests)

**Total**: 28 API tests

---

## 3️⃣ JMeter Load Tests (200 Concurrent Users)

### Prerequisites

1. **Download Apache JMeter** (if not installed):
   - Download from: https://jmeter.apache.org/download_jmeter.cgi
   - Extract to a folder
   - Version 5.6.3 recommended

2. **Ensure application is running**:
   ```bash
   curl http://localhost:8080/pos-web/login
   ```

### Option A: JMeter GUI (For Setup & Debugging)

**Windows**:
```cmd
cd C:\apache-jmeter-5.6.3\bin
jmeter.bat
```

**Linux/Mac**:
```bash
cd /path/to/jmeter/bin
./jmeter.sh
```

Then:
1. **File → Open**
2. Select: `testing/jmeter/Complete-POS-Load-Test-200-Users.jmx`
3. Click green **Start** button (▶️)
4. View results in the listeners

### Option B: Command Line (For Actual Testing)

**Windows**:
```cmd
cd C:\apache-jmeter-5.6.3\bin

REM Create results directory
mkdir results

REM Run test
jmeter -n -t "path\to\testing\jmeter\Complete-POS-Load-Test-200-Users.jmx" -l "results\test-results.jtl" -e -o "results\html-report"

REM Open report
start results\html-report\index.html
```

**Linux/Mac**:
```bash
cd /path/to/jmeter/bin

# Create results directory
mkdir -p results

# Run test
./jmeter -n \
  -t ~/latest-pos/testing/jmeter/Complete-POS-Load-Test-200-Users.jmx \
  -l results/test-results.jtl \
  -e -o results/html-report

# Open report
open results/html-report/index.html
```

### Test Configuration

- **Users**: 200 concurrent users
- **Ramp-up**: 60 seconds (gradual start)
- **Loops**: 3 iterations per user
- **Total Requests**: ~1,800 requests
- **Duration**: 5-10 minutes

### User Distribution

| Group | Users | Focus |
|-------|-------|-------|
| Sales Operations | 80 | Creating and completing sales |
| Inventory Management | 60 | Viewing and managing inventory |
| Reports & Products | 40 | Generating reports, viewing products |
| User Management | 20 | Admin user operations |

---

## 📊 Expected Results

### Unit Tests
- ✅ All 109 tests pass
- ⏱️ Completes in ~30 seconds
- 💯 100% servlet coverage

### Postman Tests
- ✅ All 28 requests succeed
- ⏱️ Response times < 500ms (most endpoints)
- ⏱️ Response times < 1500ms (reports)
- 🔒 Proper session handling
- 📝 Complete in ~2 minutes

### JMeter Load Test
- ✅ Error rate: < 1%
- ⏱️ Average response time: < 1000ms
- 📈 Throughput: > 20 req/sec
- 💪 All 200 users complete successfully
- 🕐 Completes in 5-10 minutes

---

## 🔧 Troubleshooting

### Issue: Connection Refused

**Problem**: Application not running

**Solution**:
```bash
# Check if app is accessible
curl http://localhost:8080/pos-web/login

# If not, start Tomcat
```

### Issue: Postman Tests Fail with "Session Error"

**Problem**: Session cookies not maintained

**Solution**:
1. Make sure **"Login - Valid Credentials"** runs first
2. In Postman: Settings → General → Enable **"Automatically follow redirects"**
3. Ensure cookies are enabled

### Issue: JMeter High Error Rate

**Problem**: Application can't handle load

**Solution**:
1. Check application logs:
   ```bash
   tail -f $CATALINA_HOME/logs/catalina.out
   ```
2. Verify Tomcat configuration (conf/server.xml):
   ```xml
   <Connector port="8080"
              maxThreads="200"
              acceptCount="100" />
   ```
3. Check database connection pool size
4. Start with fewer users (50) and increase gradually

### Issue: Unit Tests Fail

**Problem**: Dependencies or mocks not properly set up

**Solution**:
```bash
# Clean and rebuild
cd pos-web
mvn clean install
mvn test
```

---

## 🎯 Coverage Summary

### All 8 Servlets Tested

| Servlet | Unit Tests | Postman | JMeter | Status |
|---------|-----------|---------|---------|--------|
| LoginServlet | 10 | ✅ | ✅ | ✓ |
| LogoutServlet | 16 | ✅ | ✅ | ✓ |
| DashboardServlet | 7 | ✅ | ✅ | ✓ |
| SalesServlet | 19 | ✅ | ✅ | ✓ |
| InventoryServlet | 17 | ✅ | ✅ | ✓ |
| ReportsServlet | 16 | ✅ | ✅ | ✓ |
| ProductsServlet | 8 | ✅ | ✅ | ✓ |
| UsersServlet | 16 | ✅ | ✅ | ✓ |

**Total**: 100% coverage

---

## 📝 Test Credentials

All tests use these credentials:

- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

Make sure this user exists in your database before running tests.

---

## 🚀 Quick Test Commands

```bash
# 1. Unit tests (30 seconds)
cd pos-web && mvn clean test

# 2. Postman tests (2 minutes)
newman run testing/postman/Complete-POS-API-Tests.json

# 3. JMeter load test (5-10 minutes)
cd /path/to/jmeter/bin
./jmeter -n -t ~/latest-pos/testing/jmeter/Complete-POS-Load-Test-200-Users.jmx \
  -l results/test.jtl -e -o results/html-report
```

---

**All tests are ready to run! 🎉**

For issues or questions, check the troubleshooting section above.
