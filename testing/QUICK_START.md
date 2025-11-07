# SYOS POS System - Testing Quick Start Guide

## 🚀 Quick Setup (5 Minutes)

### Prerequisites Check
```bash
# Verify Java
java -version  # Should be 11+

# Verify Maven
mvn --version  # Should be 3.6+

# Verify MySQL
mysql --version  # Should be 8.0+
```

### 1. Database Setup
```bash
cd /home/user/latest-pos/sql
mysql -u root -p < create_database.sql
mysql -u root -p syos_db < seed_data.sql
```

### 2. Start Application
```bash
cd /home/user/latest-pos
mvn clean install
cd pos-web
mvn tomcat9:run
```

Wait for: `INFO: Starting ProtocolHandler ["http-nio-8080"]`

### 3. Verify Application
```bash
# In a new terminal
curl http://localhost:8080/pos/login
# Should return HTML content
```

---

## 🧪 Running Tests

### Option A: JMeter Performance Test (200 Concurrent Users)

```bash
cd /home/user/latest-pos/testing/jmeter

# Install JMeter first if not installed
# Download from: https://jmeter.apache.org/download_jmeter.cgi

# Run test (CLI mode - recommended)
jmeter -n -t SYOS_Performance_Test_200_Users.jmx \
       -l results/results.jtl \
       -e -o results/html-report

# View results
open results/html-report/index.html
# or
firefox results/html-report/index.html
```

**Expected Results:**
- ✅ 200 concurrent users
- ✅ Average response time < 2 seconds
- ✅ Error rate < 1%
- ✅ Throughput > 50 req/sec

---

### Option B: Postman API Tests

```bash
# Install Newman (Postman CLI)
npm install -g newman
npm install -g newman-reporter-htmlextra

# Run tests
cd /home/user/latest-pos
newman run testing/postman/SYOS_API_Tests.postman_collection.json \
       --reporters cli,htmlextra \
       --reporter-htmlextra-export testing/results/api-report.html

# View results
open testing/results/api-report.html
```

**Alternative: Use Postman Desktop**
1. Open Postman
2. Import `testing/postman/SYOS_API_Tests.postman_collection.json`
3. Click "Run Collection"
4. View results in Postman

---

### Option C: Unit Tests

```bash
cd /home/user/latest-pos

# Run all unit tests
mvn test

# Run tests for specific module
cd pos-web
mvn test

# Generate coverage report
mvn clean test jacoco:report
```

---

## 📊 Test Scenarios Included

### JMeter Test Plan
- ✅ **40 Admin Users**: Dashboard, Reports, Inventory
- ✅ **80 Cashier Users**: Sales, Billing, Transactions
- ✅ **80 Customer Users**: Browse Products, View Details

### Postman Collection (40+ Tests)
- ✅ Authentication (Login/Logout)
- ✅ Dashboard & Reports
- ✅ Sales Operations (with JSON APIs)
- ✅ Inventory Management
- ✅ Product Catalog
- ✅ User Management
- ✅ Concurrent Load Tests

---

## 🎯 Quick Performance Test

```bash
# 1. Start application
cd /home/user/latest-pos/pos-web
mvn tomcat9:run

# 2. In new terminal, run quick test
cd /home/user/latest-pos/testing/jmeter
jmeter -n -t SYOS_Performance_Test_200_Users.jmx -l results/quick-test.jtl

# 3. Check results
tail -n 20 results/quick-test.jtl
```

---

## 📈 Understanding Results

### JMeter Summary Report

```
Label         Samples  Average  Min   Max    Error%  Throughput
-------------------------------------------------------------
Admin Login      40      156    120   890     0.00%    5.2/sec
Dashboard        40      234    180  1200     0.00%    4.8/sec
Sales            80      189    140   980     0.00%   10.1/sec
Products         80      123    90    560     0.00%   12.3/sec
```

**Good Performance:**
- Average < 2000ms ✅
- Error% < 1% ✅
- Throughput > 50/sec ✅

---

## 🔧 Quick Troubleshooting

### Application Won't Start
```bash
# Check if port 8080 is already in use
lsof -i :8080
# If yes, kill the process:
kill -9 <PID>
```

### Database Connection Error
```bash
# Verify MySQL is running
sudo systemctl status mysql
# or
sudo service mysql status

# Start MySQL if needed
sudo systemctl start mysql
```

### JMeter "Command not found"
```bash
# Add JMeter to PATH
export PATH=$PATH:/path/to/jmeter/bin

# Or use full path
/path/to/jmeter/bin/jmeter -n -t test.jmx
```

### Tests Failing
```bash
# Check application logs
tail -f $CATALINA_HOME/logs/catalina.out

# Verify database connection
mysql -u root -p syos_db -e "SELECT COUNT(*) FROM users;"

# Check if application is responding
curl -I http://localhost:8080/pos/login
```

---

## 🎓 Test Users

| Role     | Username  | Password     |
|----------|-----------|--------------|
| Admin    | admin     | admin123     |
| Cashier  | cashier1  | cashier123   |
| Manager  | manager1  | manager123   |
| Customer | customer1 | customer123  |

---

## 📚 Next Steps

1. **Review Full Documentation**: `testing/README.md`
2. **Customize Tests**: Modify JMeter/Postman scripts
3. **Add More Scenarios**: Create additional test cases
4. **Set Up CI/CD**: Automate test execution
5. **Monitor Production**: Use same tests for production monitoring

---

## ⚡ One-Line Test Commands

```bash
# JMeter CLI test
jmeter -n -t testing/jmeter/SYOS_Performance_Test_200_Users.jmx -l results/test.jtl -e -o results/report

# Postman CLI test
newman run testing/postman/SYOS_API_Tests.postman_collection.json --reporters cli,htmlextra

# Maven unit tests
mvn test

# All tests (requires app running)
./testing/run-all-tests.sh
```

---

## 🆘 Need Help?

1. Check `testing/README.md` for detailed documentation
2. Review PDF reference: `CB011366 (1).pdf`
3. Check application logs: `$CATALINA_HOME/logs/catalina.out`
4. Verify database: `mysql -u root -p syos_db`

---

**Happy Testing! 🎉**

For detailed documentation, see: `testing/README.md`
