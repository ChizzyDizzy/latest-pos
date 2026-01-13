# POS System Testing Guide

Complete API and Load testing for SYOS POS System covering all 8 servlets.

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
```

**Total**: 28 API tests, 200 concurrent users load test

---

## 🌐 URL Configuration (IMPORTANT!)

**All tests are configured with the correct URLs:**

- **Base URL**: `http://localhost:8080`
- **All paths include**: `/pos-web/` prefix

**Examples**:
- Login: `http://localhost:8080/pos-web/login` ✅
- Dashboard: `http://localhost:8080/pos-web/dashboard` ✅
- Sales: `http://localhost:8080/pos-web/sales/new` ✅
- Inventory: `http://localhost:8080/pos-web/inventory/` ✅

**JMeter Configuration**:
- Server: `localhost`
- Port: `8080`
- Paths: `/pos-web/login`, `/pos-web/dashboard`, etc.

**Postman Configuration**:
- Base URL variable: `http://localhost:8080/pos-web`
- Requests use: `{{baseUrl}}/login`, `{{baseUrl}}/dashboard`, etc.

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

## 1️⃣ Postman API Tests

### ⭐ Option A: Postman GUI (Easiest!)

1. **Open Postman**
2. Click **Import** button
3. Select file: `testing/postman/Complete-POS-API-Tests.json`
4. Right-click the collection → **Run collection**
5. Click **Run**
6. ✅ View results - all 28 tests should pass!

### Option B: Newman CLI

```bash
# Install Newman (first time only)
npm install -g newman

# Run tests
newman run testing/postman/Complete-POS-API-Tests.json
```

### What Gets Tested (28 API Requests)

| Category | Tests | What's Covered |
|----------|-------|----------------|
| **Authentication** | 3 | Login (valid/invalid), Logout |
| **Dashboard** | 1 | Dashboard metrics and data |
| **Sales** | 8 | New sale, add items, complete, list, view |
| **Inventory** | 6 | List, add stock, move to shelf, low stock, expiring |
| **Reports** | 5 | Reports menu, daily sales, stock, reorder |
| **Products** | 1 | View products catalog |
| **User Management** | 3 | List users, registration form, register user (admin only) |

**Expected Results**:
- ✅ All 28 requests succeed
- ⏱️ Response times < 500ms (most endpoints)
- ⏱️ Response times < 1500ms (reports)
- 🔒 Proper session handling

---

## 2️⃣ JMeter Load Tests (200 Concurrent Users)

### Prerequisites

1. **Download Apache JMeter** (if not installed):
   - Download from: https://jmeter.apache.org/download_jmeter.cgi
   - Extract to a folder
   - Version 5.6.3 recommended

2. **Ensure application is running**:
   ```bash
   curl http://localhost:8080/pos-web/login
   ```

### ⭐ Option A: JMeter GUI (Easiest for First Run!)

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
4. Watch the results in real-time!

### Option B: Command Line (For Generating Reports)

**Windows**:
```cmd
cd C:\apache-jmeter-5.6.3\bin

REM Create results directory
mkdir results

REM Run test
jmeter -n -t "C:\path\to\testing\jmeter\Complete-POS-Load-Test-200-Users.jmx" -l "results\test-results.jtl" -e -o "results\html-report"

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

- **Total Users**: 200 concurrent users
- **Ramp-up**: 60 seconds (gradual start)
- **Loops**: 3 iterations per user
- **Total Requests**: ~1,800 requests
- **Duration**: 5-10 minutes

### User Distribution

| Thread Group | Users | % | What They Do |
|--------------|-------|---|--------------|
| **Sales Operations** | 80 | 40% | Login → Dashboard → New Sale → Add Items → Complete → List Sales → Logout |
| **Inventory Management** | 60 | 30% | Login → View Inventory → Low Stock → Expiring Items → Logout |
| **Reports & Products** | 40 | 20% | Login → Products → Daily Sales → Stock Report → Reorder → Logout |
| **User Management** | 20 | 10% | Admin Login → List Users → Registration Form → Logout |

### Expected Results

- ✅ **Error rate**: < 1%
- ⏱️ **Average response time**: < 1000ms
- 📈 **Throughput**: > 20 req/sec
- 💪 **All 200 users complete successfully**

---

## 📊 Complete Coverage

### All 8 Servlets Tested

| Servlet | Postman | JMeter | Status |
|---------|---------|---------|--------|
| LoginServlet | ✅ | ✅ | ✓ |
| LogoutServlet | ✅ | ✅ | ✓ |
| DashboardServlet | ✅ | ✅ | ✓ |
| SalesServlet | ✅ | ✅ | ✓ |
| InventoryServlet | ✅ | ✅ | ✓ |
| ReportsServlet | ✅ | ✅ | ✓ |
| ProductsServlet | ✅ | ✅ | ✓ |
| UsersServlet | ✅ | ✅ | ✓ |

**Coverage**: 100% of all servlets tested ✅

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

### Issue: JMeter XML Parsing Error

**Problem**: If you see "entity reference names can not start with character"

**Solution**: The file has already been fixed with proper XML encoding

---

## 🚀 Quick Start Commands

```bash
# 1. Postman GUI
# - Import testing/postman/Complete-POS-API-Tests.json
# - Run collection
# - Done!

# 2. Postman CLI
npm install -g newman
newman run testing/postman/Complete-POS-API-Tests.json

# 3. JMeter GUI
cd /path/to/jmeter/bin
./jmeter.sh
# File → Open → testing/jmeter/Complete-POS-Load-Test-200-Users.jmx
# Click Start

# 4. JMeter CLI (Windows)
cd C:\apache-jmeter-5.6.3\bin
mkdir results
jmeter -n -t "C:\path\to\testing\jmeter\Complete-POS-Load-Test-200-Users.jmx" -l "results\test.jtl" -e -o "results\html-report"
start results\html-report\index.html
```

---

## 📝 Test Credentials

All tests use:

- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

Make sure this user exists in your database before running tests.

---

## ✨ Summary

This testing suite provides **complete coverage** for your POS system:

- **28 API tests** covering all endpoints
- **200 concurrent users** simulating real load
- **All 8 servlets** fully tested
- **Proper URL configuration**: `http://localhost:8080/pos-web/*`

**Both Postman and JMeter are ready to use - just import and run!** 🎉

---

For issues or questions, check the troubleshooting section above.
