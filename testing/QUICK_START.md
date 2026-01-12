# Quick Start Guide - FIXED for /pos-web Context Path

All tests are now correctly configured for: `http://localhost:8080/pos-web/`

---

## ✅ What Was Fixed

### JMeter Test Plan
**Problem**: Paths like `/login` were replacing the base path instead of appending
**Solution**: Changed to relative paths like `login` that append to `/pos-web`

**Before**: `/login` → `http://localhost:8080/login` ❌
**After**: `login` → `http://localhost:8080/pos-web/login` ✅

### Postman Collection
**Status**: Already correctly configured with `baseUrl = http://localhost:8080/pos-web` ✅

---

## 🚀 Running the Tests

### 1. JUnit Unit Tests (30 seconds)

```bash
cd pos-web
mvn clean test
```

**Expected Output**:
```
Tests run: 109, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

### 2. Postman API Tests (2 minutes)

#### Option A: Postman GUI

1. Open Postman
2. Click **Import**
3. Select file: `testing/postman/Complete-POS-API-Tests.json`
4. Right-click collection → **Run collection**
5. Click **Run Complete POS System API Tests**

#### Option B: Newman CLI

```bash
# Install Newman (first time only)
npm install -g newman

# Run tests
newman run testing/postman/Complete-POS-API-Tests.json
```

**Expected Output**: All 28 tests pass ✅

---

### 3. JMeter Load Test (5-10 minutes)

#### Prerequisites

Make sure your application is running:
```bash
# Test the login page
curl http://localhost:8080/pos-web/login
# Should return HTML (200 OK)
```

#### Option A: JMeter GUI Mode (Recommended for first run)

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
1. **File → Open** → Select `Complete-POS-Load-Test-200-Users.jmx`
2. Click green **Start** button (▶️)
3. View results in the listeners

#### Option B: Command Line Mode (For actual testing)

**Windows**:
```cmd
cd C:\apache-jmeter-5.6.3\bin

REM Create results directory
mkdir "C:\path\to\latest-pos\testing\jmeter\results"

REM Run test
jmeter -n ^
  -t "C:\path\to\latest-pos\testing\jmeter\Complete-POS-Load-Test-200-Users.jmx" ^
  -l "C:\path\to\latest-pos\testing\jmeter\results\test-results.jtl" ^
  -e -o "C:\path\to\latest-pos\testing\jmeter\results\html-report"

REM View report
start "C:\path\to\latest-pos\testing\jmeter\results\html-report\index.html"
```

**Linux/Mac**:
```bash
cd /path/to/jmeter/bin

# Create results directory
mkdir -p ~/latest-pos/testing/jmeter/results

# Run test
./jmeter -n \
  -t ~/latest-pos/testing/jmeter/Complete-POS-Load-Test-200-Users.jmx \
  -l ~/latest-pos/testing/jmeter/results/test-results.jtl \
  -e -o ~/latest-pos/testing/jmeter/results/html-report

# View report
open ~/latest-pos/testing/jmeter/results/html-report/index.html
```

---

## 📊 Test Configuration

### JMeter Load Distribution

| Thread Group | Users | % | What They Do |
|--------------|-------|---|--------------|
| Sales Operations | 80 | 40% | Login → Dashboard → New Sale → Add Items → Complete → List Sales → Logout |
| Inventory Operations | 60 | 30% | Login → View Inventory → Low Stock → Expiring Items → Logout |
| Reports and Products | 40 | 20% | Login → Products → Daily Sales → Stock Report → Reorder → Logout |
| User Management | 20 | 10% | Admin Login → List Users → Registration Form → Logout |

### Test Parameters

- **Total Users**: 200 concurrent users
- **Ramp-up**: 60 seconds (gradual start)
- **Loops**: 3 iterations per user
- **Total Requests**: ~1,800 requests
- **Duration**: 5-10 minutes

---

## ✅ Verification Checklist

Before running load tests:

- [ ] Application deployed to Tomcat
- [ ] Accessible at `http://localhost:8080/pos-web/`
- [ ] Database is running and populated
- [ ] Admin user exists (admin/admin123)
- [ ] Test data includes:
  - [ ] Items with ON_SHELF status
  - [ ] Items with different quantities
  - [ ] Some expired items
  - [ ] Low stock items

**Quick Test**:
```bash
# Should return 200 OK with HTML
curl -I http://localhost:8080/pos-web/login
```

---

## 📈 Expected Results

### JUnit Tests
- ✅ 109 tests pass
- ✅ No failures or errors
- ⏱️ Completes in ~30 seconds

### Postman Tests
- ✅ All 28 requests succeed
- ✅ Response times <500ms (most)
- ✅ Proper session handling
- ⏱️ Completes in ~2 minutes

### JMeter Load Test
- ✅ Error rate: <1%
- ✅ Avg response time: <1000ms
- ✅ Throughput: >20 req/sec
- ✅ All 200 users complete successfully
- ⏱️ Completes in 5-10 minutes

---

## 🔧 Troubleshooting

### JMeter: "Connection Refused"
**Problem**: Application not running
**Fix**:
```bash
curl http://localhost:8080/pos-web/login
# If fails, start Tomcat
```

### JMeter: "404 Not Found"
**Problem**: Wrong context path
**Fix**: Already fixed! Paths now use `/pos-web/` correctly

### Postman: "401 Unauthorized"
**Problem**: Session not maintained
**Fix**:
1. Run "Login - Valid Credentials" first
2. Ensure "Automatically follow redirects" is enabled in Postman settings

### High Error Rate in JMeter
**Possible causes**:
1. Database connection pool too small
2. Tomcat maxThreads < 200
3. Application errors under load

**Check logs**:
```bash
tail -f $CATALINA_HOME/logs/catalina.out
```

---

## 📝 Summary of All URLs

All tests now correctly use these URLs:

| Endpoint | Full URL |
|----------|----------|
| Login | `http://localhost:8080/pos-web/login` |
| Dashboard | `http://localhost:8080/pos-web/dashboard` |
| New Sale | `http://localhost:8080/pos-web/sales/new` |
| Add Item | `http://localhost:8080/pos-web/sales/add-item` |
| Complete Sale | `http://localhost:8080/pos-web/sales/complete` |
| List Sales | `http://localhost:8080/pos-web/sales/list` |
| View Inventory | `http://localhost:8080/pos-web/inventory/` |
| Low Stock | `http://localhost:8080/pos-web/inventory/low-stock` |
| Expiring Items | `http://localhost:8080/pos-web/inventory/expiring` |
| Products | `http://localhost:8080/pos-web/products` |
| Daily Sales Report | `http://localhost:8080/pos-web/reports/daily-sales` |
| Stock Report | `http://localhost:8080/pos-web/reports/stock` |
| Reorder Report | `http://localhost:8080/pos-web/reports/reorder` |
| List Users | `http://localhost:8080/pos-web/users` |
| Register User | `http://localhost:8080/pos-web/users/register` |
| Logout | `http://localhost:8080/pos-web/logout` |

---

**All tests are now ready to run! 🎉**

Download the latest files from your branch: `claude/create-test-plan-Bli0q`
