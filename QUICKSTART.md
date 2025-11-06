# 🚀 QUICKSTART GUIDE - SYOS POS System

## ✅ What's New

Your POS system has been **successfully refactored** into a professional multi-module web application!

### 📦 Three Modules Created

1. **pos-core** - Business logic library (thread-safe)
2. **pos-cli** - Command-line interface (original CLI preserved)
3. **pos-web** - Web application with Servlets + JSP (NEW!)

---

## ⚡ Quick Start (3 Steps)

### Step 1: Setup Database (One-Time)

```bash
cd /home/user/syos-pos-system-chirath
mysql -u root -p < sql/create_database.sql
```

Enter MySQL password when prompted.

### Step 2: Build Everything

```bash
mvn clean install
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] SYOS POS System - Parent ..................... SUCCESS
[INFO] POS Core ..................................... SUCCESS
[INFO] POS CLI ...................................... SUCCESS
[INFO] POS Web Application .......................... SUCCESS
```

### Step 3: Choose Your Interface

#### Option A: Run CLI Application

```bash
cd pos-cli
java -jar target/pos-cli.jar
```

Login: `admin` / `admin123`

#### Option B: Run Web Application (Recommended)

**With your Tomcat 9:**

```bash
# Copy WAR to Tomcat
cp pos-web/target/pos-web.war $CATALINA_HOME/webapps/

# Start Tomcat
$CATALINA_HOME/bin/startup.sh

# Open browser
http://localhost:8080/pos-web/
```

**Or use Maven plugin (for testing):**

```bash
cd pos-web
mvn tomcat9:run

# Open browser
http://localhost:8080/pos/
```

---

## 🌐 Web Application Login

Navigate to: **http://localhost:8080/pos-web/**

### Demo Users

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| manager1 | manager123 | MANAGER |
| cashier1 | cashier123 | CASHIER |

---

## 🎯 Key Features

### CLI Features (Preserved)
- ✅ Create sales
- ✅ Manage inventory
- ✅ Generate reports
- ✅ User management

### Web Features (NEW!)
- ✅ **Dashboard**: Real-time metrics, alerts
- ✅ **Sales**: Create sales, view bills, concurrent transactions
- ✅ **Inventory**: Add stock, track expiry, low stock alerts
- ✅ **Reports**: Daily sales, stock reports, reorder alerts
- ✅ **Multi-User**: Multiple users can work simultaneously
- ✅ **Authentication**: Secure login/logout
- ✅ **Thread-Safe**: Handles concurrent operations safely

---

## 🔧 Project Structure

```
/home/user/syos-pos-system-chirath/
├── pom.xml                    # Parent POM
├── pos-core/                  # Shared business logic
├── pos-cli/                   # CLI application
├── pos-web/                   # Web application
├── sql/                       # Database scripts
├── README-REFACTORED.md       # Complete documentation
├── REFACTORING-GUIDE.md       # Architecture details
└── QUICKSTART.md              # This file
```

---

## 📊 Testing Concurrent Access

1. **Open 3 browser tabs**
2. **Login as different users:**
   - Tab 1: `admin` / `admin123`
   - Tab 2: `manager1` / `manager123`
   - Tab 3: `cashier1` / `cashier123`
3. **All three can:**
   - View dashboard
   - Create sales simultaneously
   - Manage inventory
   - Generate reports

**Thread Safety:** The system prevents race conditions and ensures data integrity!

---

## 🐛 Troubleshooting

### Build Failed?

```bash
# Check Java version (need 17+)
java -version

# Check Maven version (need 3.8+)
mvn -version
```

### Database Connection Failed?

```bash
# Check MySQL is running
sudo systemctl status mysql

# Test connection
mysql -u root -p syos_db

# Update credentials in:
# pos-core/src/main/resources/config/application.properties
```

### Port 8080 In Use?

```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <PID>

# Or change Tomcat port in server.xml
```

---

## 📚 Full Documentation

- **README-REFACTORED.md** - Complete build and deployment guide
- **REFACTORING-GUIDE.md** - Architecture and concurrency explained
- **sql/create_database.sql** - Database schema

---

## 🎓 Architecture Highlights

### Before (Single Module)
```
CLI App (Monolith)
└── Everything mixed together
```

### After (Multi-Module)
```
┌──────────┐     ┌──────────┐
│ pos-cli  │     │ pos-web  │
│   (CLI)  │     │   (Web)  │
└────┬─────┘     └────┬─────┘
     │                │
     └────┬───────────┘
          │
     ┌────▼─────┐
     │ pos-core │ ← Shared Logic
     │ (Library)│
     └────┬─────┘
          │
     ┌────▼─────┐
     │  MySQL   │
     └──────────┘
```

### Concurrency Features

- **Tomcat Thread Pool**: Handles 200+ concurrent requests
- **Thread-Safe Services**: Synchronized methods prevent race conditions
- **Connection Pool**: 5-10 concurrent database connections
- **Session Isolation**: Each user's data is isolated
- **Double-Check Locking**: Prevents inventory overselling

---

## ⚙️ Maven Commands

```bash
# Build all modules
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests

# Build specific module
cd pos-core && mvn clean install
cd pos-cli && mvn clean package
cd pos-web && mvn clean package

# Run tests
mvn test

# Run CLI
java -jar pos-cli/target/pos-cli.jar

# Package web app
cd pos-web && mvn package
# Output: pos-web/target/pos-web.war
```

---

## 🎉 Success Indicators

### ✅ Build Successful
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### ✅ CLI Running
```
Starting SYOS POS System - CLI Application...
Database connection pool initialized
=== MAIN MENU ===
```

### ✅ Web App Running
```
INFO  AppContextListener - === SYOS POS Web Application Starting ===
INFO  AppContextListener - ✓ Database connection pool initialized
INFO  AppContextListener - === Application Ready for Concurrent Access ===
```

### ✅ Web Login Works
- Navigate to http://localhost:8080/pos-web/
- See login page
- Login with `admin` / `admin123`
- See dashboard with metrics

---

## 🚀 Next Steps

1. **Explore the web interface:**
   - Create a test sale
   - Add inventory items
   - Generate reports
   - Try multiple concurrent users

2. **Test the CLI:**
   - Run the CLI application
   - Verify it uses the same database
   - Both CLI and Web can work simultaneously!

3. **Review the code:**
   - Check out the clean module structure
   - Review thread-safe services
   - Explore MVC architecture in pos-web

4. **Deploy to production:**
   - Copy WAR to production Tomcat
   - Configure database credentials
   - Enable HTTPS
   - Monitor thread pool performance

---

## 💡 Tips

- **Start with Web UI** - It's more user-friendly
- **Use CLI for automation** - Great for scripts
- **Both use same database** - Data is shared
- **Test concurrency** - Open multiple browser tabs
- **Check logs** - Monitor `$CATALINA_HOME/logs/catalina.out`

---

## 📞 Need Help?

1. Check logs:
   - Tomcat: `$CATALINA_HOME/logs/catalina.out`
   - Application: Console output

2. Review documentation:
   - README-REFACTORED.md
   - REFACTORING-GUIDE.md

3. Verify setup:
   - Java 17+ installed
   - Maven 3.8+ installed
   - MySQL 8.0+ running
   - Database created

---

## 🎊 Congratulations!

You now have a **production-ready, multi-threaded POS system** with:

✅ Concurrent user support
✅ Thread-safe operations
✅ MVC architecture
✅ Both CLI and Web interfaces
✅ Shared business logic
✅ Professional structure

**Enjoy your refactored POS system!** 🚀

---

*Last Updated: 2025-11-06*
*SYOS POS System v1.0-SNAPSHOT*
