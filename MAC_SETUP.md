# MAC SETUP GUIDE - SYOS POS SYSTEM

Complete setup instructions for running the POS system on macOS.

---

## TABLE OF CONTENTS
1. [Prerequisites](#prerequisites)
2. [Install Required Software](#install-required-software)
3. [Database Setup](#database-setup)
4. [Project Setup](#project-setup)
5. [Deploy to Tomcat](#deploy-to-tomcat)
6. [Running the Application](#running-the-application)
7. [Testing Setup](#testing-setup)
8. [Troubleshooting](#troubleshooting)

---

## PREREQUISITES

**What you need:**
- macOS 10.14+ (Mojave or later)
- Administrative access (sudo privileges)
- Internet connection (for downloading software)
- At least 4GB free disk space

---

## INSTALL REQUIRED SOFTWARE

### 1. Install Homebrew (if not already installed)

Homebrew is a package manager for Mac.

```bash
# Check if Homebrew is installed
which brew

# If not installed, install Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Verify installation
brew --version
```

### 2. Install Java 17

```bash
# Install OpenJDK 17
brew install openjdk@17

# Create symlink
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Set JAVA_HOME in your shell profile
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc

# Reload shell configuration
source ~/.zshrc

# Verify Java installation
java -version
# Should show: openjdk version "17.x.x"

javac -version
# Should show: javac 17.x.x
```

**Alternative: Manual Download**
- Download from: https://adoptium.net/temurin/releases/?version=17
- Choose macOS package (.pkg or .dmg)
- Install and set JAVA_HOME as above

### 3. Install Maven

```bash
# Install Maven
brew install maven

# Verify installation
mvn -version
# Should show Maven 3.8+ and Java 17

# If JAVA_HOME error, run:
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn -version
```

### 4. Install MySQL

```bash
# Install MySQL 8
brew install mysql

# Start MySQL service
brew services start mysql

# Verify MySQL is running
brew services list | grep mysql
# Should show: mysql started

# Secure MySQL installation (set root password)
mysql_secure_installation

# Follow prompts:
# - Set root password: your_password (remember this!)
# - Remove anonymous users: Yes
# - Disallow root login remotely: Yes
# - Remove test database: Yes
# - Reload privilege tables: Yes

# Test MySQL connection
mysql -u root -p
# Enter the password you just set
# You should see: mysql>

# Exit MySQL
exit;
```

**If MySQL doesn't start:**
```bash
# Check error logs
tail -f /opt/homebrew/var/mysql/*.err

# Or try starting manually
mysql.server start
```

### 5. Install Apache Tomcat 9

```bash
# Download Tomcat 9 (latest version)
cd ~/Downloads
curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.85/bin/apache-tomcat-9.0.85.tar.gz

# Extract to /usr/local
sudo mkdir -p /usr/local
sudo tar -xzf apache-tomcat-9.0.85.tar.gz -C /usr/local/

# Rename for convenience
sudo mv /usr/local/apache-tomcat-9.0.85 /usr/local/tomcat9

# Set permissions
sudo chmod +x /usr/local/tomcat9/bin/*.sh

# Create CATALINA_HOME environment variable
echo 'export CATALINA_HOME=/usr/local/tomcat9' >> ~/.zshrc
echo 'export PATH="$CATALINA_HOME/bin:$PATH"' >> ~/.zshrc

# Reload shell
source ~/.zshrc

# Verify installation
catalina.sh version
# Should show Tomcat version info
```

**Configure Tomcat Manager (optional but recommended):**

```bash
# Edit tomcat-users.xml
sudo nano /usr/local/tomcat9/conf/tomcat-users.xml

# Add before </tomcat-users>:
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<user username="admin" password="admin123" roles="manager-gui,manager-script"/>

# Save: Ctrl+O, Enter, Ctrl+X
```

### 6. Install Postman

**Option A: Homebrew**
```bash
brew install --cask postman
```

**Option B: Manual Download**
- Download from: https://www.postman.com/downloads/
- Drag to Applications folder
- Open from Applications

### 7. Install Apache JMeter

```bash
# Install JMeter
brew install jmeter

# Verify installation
jmeter --version
# Should show JMeter version 5.6+

# Launch JMeter GUI
jmeter
# GUI should open
```

**Alternative: Manual Download**
```bash
# Download from: https://jmeter.apache.org/download_jmeter.cgi
cd ~/Downloads
curl -O https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz

# Extract
tar -xzf apache-jmeter-5.6.3.tgz
sudo mv apache-jmeter-5.6.3 /usr/local/jmeter

# Add to PATH
echo 'export PATH="/usr/local/jmeter/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify
jmeter --version
```

---

## DATABASE SETUP

### 1. Create Database and Tables

```bash
# Navigate to project directory
cd /path/to/latest-pos

# Run database creation script
mysql -u root -p < sql/create_database.sql

# Enter your MySQL root password when prompted
```

**What this does:**
- Creates database `pos_db`
- Creates 6 tables (users, items, bills, bill_items, stock_movements, audit_log)
- Inserts sample data (users, products)

### 2. Verify Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Use the database
USE pos_db;

# Check tables
SHOW TABLES;
# Should show: audit_log, bill_items, bills, items, stock_movements, users

# Check sample users
SELECT username, role FROM users;
# Should show: admin, manager_new, cashier_new, customer_new

# Check sample items
SELECT code, name, price, quantity FROM items LIMIT 5;
# Should show sample products

# Exit
exit;
```

### 3. Configure Database Connection (if needed)

If you used a different MySQL password, update the connection string:

```bash
# Edit DatabaseConfig.java
nano pos-core/src/main/java/com/syos/infrastructure/config/DatabaseConfig.java

# Update these lines if needed:
# private static final String URL = "jdbc:mysql://localhost:3306/pos_db";
# private static final String USER = "root";
# private static final String PASSWORD = "your_actual_password";

# Or set environment variables:
export DB_URL="jdbc:mysql://localhost:3306/pos_db"
export DB_USER="root"
export DB_PASSWORD="your_password"
```

---

## PROJECT SETUP

### 1. Clone/Extract Project

```bash
# If you have the project as a ZIP
cd ~/Downloads
unzip latest-pos.zip -d ~/Documents/

# Navigate to project
cd ~/Documents/latest-pos

# Or if you're already in the project directory
pwd
# Should show: /path/to/latest-pos
```

### 2. Build the Project

```bash
# Navigate to project root
cd ~/Documents/latest-pos

# Clean and build all modules
mvn clean install

# This will:
# 1. Compile pos-core (JAR)
# 2. Compile pos-web (WAR)
# 3. Run tests (if any)
# 4. Package into deployable files

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX s

# Check the WAR file was created
ls -lh pos-web/target/pos-web.war
# Should show: pos-web.war (around 5-10 MB)
```

**If build fails:**

```bash
# Check Java version
java -version  # Should be 17

# Check Maven uses correct Java
mvn -version   # Should show Java 17

# If wrong Java version
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean install
```

---

## DEPLOY TO TOMCAT

### 1. Deploy WAR File

```bash
# Stop Tomcat if running
catalina.sh stop

# Remove old deployment (if exists)
sudo rm -rf /usr/local/tomcat9/webapps/pos-web*

# Copy WAR to Tomcat webapps
sudo cp pos-web/target/pos-web.war /usr/local/tomcat9/webapps/

# Start Tomcat
catalina.sh run

# Watch deployment logs
# Tomcat will automatically extract pos-web.war
# Look for: "Deployment of web application archive [pos-web.war] has finished"
```

**Alternative: Start Tomcat in background**

```bash
# Start Tomcat as background process
catalina.sh start

# View logs
tail -f /usr/local/tomcat9/logs/catalina.out

# Stop background Tomcat
catalina.sh stop
```

### 2. Verify Deployment

```bash
# Check if WAR was extracted
ls -la /usr/local/tomcat9/webapps/
# Should show:
# - pos-web.war (original file)
# - pos-web/ (extracted directory)

# Check logs for errors
tail -n 100 /usr/local/tomcat9/logs/catalina.out

# Look for:
# "INFO: Deployment of web application archive [pos-web.war] has finished in [X] ms"
```

### 3. Access Application in Browser

```bash
# Open browser to:
open http://localhost:8080/pos-web/login

# Or manually open browser and go to:
# http://localhost:8080/pos-web/login
```

**Login with:**
- Username: `admin`
- Password: `admin123`

---

## RUNNING THE APPLICATION

### Starting Everything

**Terminal 1: MySQL (if not running as service)**
```bash
# Check if MySQL is running
brew services list | grep mysql

# If not running
brew services start mysql

# Or manually
mysql.server start
```

**Terminal 2: Tomcat**
```bash
# Start Tomcat with logs visible
cd /usr/local/tomcat9
./bin/catalina.sh run

# Or in background
./bin/catalina.sh start

# To stop
./bin/catalina.sh stop
```

**Browser:**
```bash
# Open application
open http://localhost:8080/pos-web/login

# Login as admin
# Username: admin
# Password: admin123
```

### Stopping Everything

```bash
# Stop Tomcat
catalina.sh stop

# Stop MySQL (if not needed)
brew services stop mysql
# Or
mysql.server stop
```

---

## TESTING SETUP

### 1. Postman Setup

**Import Collection:**

1. Open Postman
2. Click "Import" (top left)
3. Select file: `~/Documents/latest-pos/testing/postman/Complete-POS-API-Tests.json`
4. Click "Import"

**Set Environment Variable:**

1. Click "Environments" (left sidebar)
2. Click "+" to create new environment
3. Name: "POS Local"
4. Add variable:
   - **Variable:** `baseUrl`
   - **Initial Value:** `http://localhost:8080/pos-web`
   - **Current Value:** `http://localhost:8080/pos-web`
5. Click "Save"
6. Select "POS Local" from environment dropdown (top right)

**Run Tests:**

1. Click "Collections" → "Complete POS API Tests"
2. Click "Run" button
3. Click "Run Complete POS API Tests"
4. Watch tests execute (28 tests should pass)

**Command Line (Newman):**

```bash
# Install Newman (Postman CLI)
npm install -g newman

# If npm not installed
brew install node
npm install -g newman

# Run tests
cd ~/Documents/latest-pos
newman run testing/postman/Complete-POS-API-Tests.json \
  --env-var "baseUrl=http://localhost:8080/pos-web"

# Results will display in terminal
```

### 2. JMeter Setup

**GUI Mode:**

```bash
# Launch JMeter
jmeter

# Open test plan
# File → Open → Select: ~/Documents/latest-pos/testing/jmeter/Complete-POS-Load-Test-200-Users.jmx

# Add listeners (if not present)
# Right-click "Complete POS System - 200 Users"
# Add → Listener → "View Results Tree"
# Add → Listener → "Summary Report"
# Add → Listener → "Aggregate Report"

# Run test
# Click green "Start" button (top toolbar, play icon)

# View results
# Click "Summary Report" or "Aggregate Report"
```

**Command Line (Non-GUI - Recommended):**

```bash
# Create results directory
mkdir -p ~/Documents/latest-pos/results

# Run test
cd ~/Documents/latest-pos
jmeter -n -t testing/jmeter/Complete-POS-Load-Test-200-Users.jmx \
       -l results/test-results.jtl \
       -e -o results/html-report

# View HTML report
open results/html-report/index.html

# Parameters explained:
# -n : Non-GUI mode (faster, less memory)
# -t : Test plan file
# -l : Results log file (JTL format)
# -e -o : Generate HTML dashboard report
```

### 3. Quick Test Script

Create a test script for easy testing during viva:

```bash
# Create script
nano ~/Documents/latest-pos/run-tests.sh

# Add content:
#!/bin/bash
echo "=== POS System Testing ==="
echo ""
echo "1. Testing application accessibility..."
curl -s http://localhost:8080/pos-web/login > /dev/null && echo "✓ Application is running" || echo "✗ Application not accessible"
echo ""
echo "2. Running Postman API tests..."
newman run testing/postman/Complete-POS-API-Tests.json --env-var "baseUrl=http://localhost:8080/pos-web" --reporters cli,json --reporter-json-export results/postman-results.json
echo ""
echo "3. Running JMeter load test (200 users)..."
jmeter -n -t testing/jmeter/Complete-POS-Load-Test-200-Users.jmx -l results/jmeter-results.jtl -j results/jmeter.log
echo ""
echo "=== Testing Complete ==="
echo "View results:"
echo "- Postman: cat results/postman-results.json"
echo "- JMeter: cat results/jmeter-results.jtl"

# Make executable
chmod +x ~/Documents/latest-pos/run-tests.sh

# Run tests
cd ~/Documents/latest-pos
./run-tests.sh
```

---

## TROUBLESHOOTING

### Issue 1: "Port 8080 already in use"

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change Tomcat port
sudo nano /usr/local/tomcat9/conf/server.xml
# Change: <Connector port="8080" to <Connector port="8081"
# Update application URL to http://localhost:8081/pos-web/
```

### Issue 2: "Can't connect to MySQL server"

```bash
# Check if MySQL is running
brew services list | grep mysql

# Start MySQL
brew services start mysql

# Or manually
mysql.server start

# Check MySQL error log
tail -f /opt/homebrew/var/mysql/*.err

# Test connection
mysql -u root -p

# If "Access denied"
# Reset root password:
brew services stop mysql
mysqld_safe --skip-grant-tables &
mysql -u root
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
mysql> FLUSH PRIVILEGES;
mysql> exit;
killall mysqld
brew services start mysql
```

### Issue 3: "JAVA_HOME not set"

```bash
# Find Java installation
/usr/libexec/java_home -V

# Set JAVA_HOME for Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Make permanent
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify
echo $JAVA_HOME
java -version
```

### Issue 4: "mvn: command not found"

```bash
# Install Maven
brew install maven

# Add to PATH if needed
echo 'export PATH="/opt/homebrew/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify
mvn -version
```

### Issue 5: Tomcat won't start

```bash
# Check if already running
ps aux | grep tomcat

# Kill if found
pkill -f tomcat

# Check CATALINA_HOME
echo $CATALINA_HOME
# Should be: /usr/local/tomcat9

# Set if not set
export CATALINA_HOME=/usr/local/tomcat9

# Check permissions
ls -la /usr/local/tomcat9/bin/*.sh
# Should be executable (rwxr-xr-x)

# Make executable if needed
sudo chmod +x /usr/local/tomcat9/bin/*.sh

# Check logs
tail -f /usr/local/tomcat9/logs/catalina.out

# Start with debug
catalina.sh run  # (not start, so you see output)
```

### Issue 6: "Application deployed but 404 error"

```bash
# Check WAR was extracted
ls -la /usr/local/tomcat9/webapps/pos-web/

# If directory doesn't exist, manually extract
cd /usr/local/tomcat9/webapps
sudo jar -xvf pos-web.war

# Check web.xml exists
ls -la /usr/local/tomcat9/webapps/pos-web/WEB-INF/web.xml

# Check Tomcat logs
tail -f /usr/local/tomcat9/logs/catalina.out
# Look for errors during deployment

# Try correct URL
open http://localhost:8080/pos-web/login
# NOT: http://localhost:8080/login
# NOT: http://localhost:8080/pos-web
```

### Issue 7: Database connection errors in application

```bash
# Check MySQL is accessible
mysql -u root -p pos_db

# Verify database exists
mysql -u root -p -e "SHOW DATABASES;" | grep pos_db

# Recreate if needed
mysql -u root -p < sql/create_database.sql

# Check connection settings in code
# pos-core/src/main/java/com/syos/infrastructure/config/DatabaseConfig.java
# Verify: jdbc:mysql://localhost:3306/pos_db
# Verify: user = root
# Verify: password matches your MySQL password
```

### Issue 8: JMeter tests fail with connection errors

```bash
# Ensure application is running
curl -I http://localhost:8080/pos-web/login

# Check JMeter HTTP defaults
# Open JMeter → Load test plan → Click "HTTP Request Defaults"
# Verify: Server Name = localhost
# Verify: Port = 8080

# Check individual requests
# Click on any HTTP Request
# Verify: Path starts with /pos-web/

# Run with verbose logging
jmeter -n -t testing/jmeter/Complete-POS-Load-Test-200-Users.jmx \
       -l results.jtl -j jmeter.log

# Check log
cat jmeter.log
```

### Issue 9: Postman tests fail

```bash
# Verify baseUrl variable is set
# Postman → Environments → POS Local
# baseUrl = http://localhost:8080/pos-web (no trailing slash)

# Test application manually
curl http://localhost:8080/pos-web/login

# Run tests in order (authentication first)
# Some tests depend on being logged in

# Check for errors in Postman console
# View → Show Postman Console
# Run tests and check console output
```

### Issue 10: Out of memory errors

```bash
# Increase Java heap for Maven
export MAVEN_OPTS="-Xmx2048m"
mvn clean install

# Increase heap for JMeter
export HEAP="-Xms1g -Xmx4g"
jmeter ...

# Or edit JMeter config
nano /usr/local/jmeter/bin/jmeter.sh
# Change: HEAP="-Xms1g -Xmx4g"

# Increase Tomcat memory
nano /usr/local/tomcat9/bin/catalina.sh
# Add after "#!/bin/sh":
# JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx2048m"
```

---

## QUICK START CHECKLIST

**Before Viva:**

- [ ] Install all prerequisites (Java 17, Maven, MySQL, Tomcat, Postman, JMeter)
- [ ] Create database: `mysql -u root -p < sql/create_database.sql`
- [ ] Build project: `mvn clean install`
- [ ] Deploy to Tomcat: Copy `pos-web.war` to Tomcat webapps
- [ ] Start MySQL: `brew services start mysql`
- [ ] Start Tomcat: `catalina.sh run`
- [ ] Test application: Open `http://localhost:8080/pos-web/login`
- [ ] Login as admin/admin123
- [ ] Import Postman collection
- [ ] Load JMeter test plan
- [ ] Run quick test: `./run-tests.sh` (if you created it)

**During Viva:**

- [ ] Application accessible at `http://localhost:8080/pos-web/login`
- [ ] MySQL running: `brew services list | grep mysql`
- [ ] Tomcat running: `ps aux | grep tomcat`
- [ ] Postman ready with collection loaded
- [ ] JMeter ready with test plan loaded
- [ ] Terminal open to show logs if needed

---

## USEFUL COMMANDS REFERENCE

```bash
# MySQL
brew services start mysql              # Start MySQL
brew services stop mysql               # Stop MySQL
mysql -u root -p                       # Login to MySQL
mysql -u root -p pos_db                # Login to pos_db database

# Tomcat
catalina.sh run                        # Start Tomcat (foreground)
catalina.sh start                      # Start Tomcat (background)
catalina.sh stop                       # Stop Tomcat
tail -f $CATALINA_HOME/logs/catalina.out  # View logs

# Maven
mvn clean install                      # Build project
mvn clean package                      # Package without install
mvn clean                              # Clean build artifacts

# Application
open http://localhost:8080/pos-web/login  # Open in browser
curl http://localhost:8080/pos-web/login  # Test with curl

# Postman
newman run testing/postman/Complete-POS-API-Tests.json  # Run tests

# JMeter
jmeter                                 # Open GUI
jmeter -n -t file.jmx -l results.jtl  # Run in CLI

# Process Management
lsof -i :8080                          # Check port 8080
kill -9 <PID>                          # Kill process
ps aux | grep tomcat                   # Find Tomcat process
ps aux | grep mysql                    # Find MySQL process

# File System
cd ~/Documents/latest-pos              # Go to project
ls -la /usr/local/tomcat9/webapps/     # List Tomcat apps
tail -f file.log                       # Follow log file
```

---

## ENVIRONMENT VARIABLES SUMMARY

Add these to `~/.zshrc` (or `~/.bash_profile` if using bash):

```bash
# Java
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

# Tomcat
export CATALINA_HOME=/usr/local/tomcat9
export PATH="$CATALINA_HOME/bin:$PATH"

# JMeter (if manual install)
export PATH="/usr/local/jmeter/bin:$PATH"

# Maven
export MAVEN_OPTS="-Xmx2048m"

# Database (optional)
export DB_URL="jdbc:mysql://localhost:3306/pos_db"
export DB_USER="root"
export DB_PASSWORD="your_password"
```

Apply changes:
```bash
source ~/.zshrc
```

---

## VIVA DAY SETUP (30 Minutes Before)

```bash
# 1. Start MySQL
brew services start mysql

# 2. Verify database
mysql -u root -p -e "USE pos_db; SELECT COUNT(*) FROM users;"

# 3. Navigate to project
cd ~/Documents/latest-pos

# 4. Build project (if any changes)
mvn clean install

# 5. Deploy to Tomcat
sudo cp pos-web/target/pos-web.war /usr/local/tomcat9/webapps/
sudo rm -rf /usr/local/tomcat9/webapps/pos-web  # Remove old deployment

# 6. Start Tomcat (in background)
catalina.sh start

# 7. Wait for deployment (30 seconds)
sleep 30

# 8. Test application
open http://localhost:8080/pos-web/login

# 9. Login and verify
# Username: admin
# Password: admin123

# 10. Open Postman
open -a Postman

# 11. Open JMeter
jmeter &

# 12. Load test plans in both tools

# 13. Keep terminal ready with logs
tail -f /usr/local/tomcat9/logs/catalina.out
```

---

## ADDITIONAL RESOURCES

**Documentation:**
- Java 17 Docs: https://docs.oracle.com/en/java/javase/17/
- Maven Guide: https://maven.apache.org/guides/
- Tomcat 9 Docs: https://tomcat.apache.org/tomcat-9.0-doc/
- MySQL 8 Docs: https://dev.mysql.com/doc/refman/8.0/en/
- Postman Docs: https://learning.postman.com/
- JMeter Docs: https://jmeter.apache.org/usermanual/

**Troubleshooting:**
- Stack Overflow: https://stackoverflow.com/
- Tomcat FAQ: https://wiki.apache.org/tomcat/FAQ

**Your Project Docs:**
- Testing Guide: `testing/README.md`
- Viva Preparation: `VIVA_PREPARATION.md`
- Database Schema: `sql/create_database.sql`

---

## GOOD LUCK WITH YOUR VIVA! 🍀

**Remember:**
- Test everything 30 minutes before
- Keep terminals open with logs
- Have backup plan (screenshots, video recording)
- Stay calm and confident
- You've got this!

**Quick Health Check:**
```bash
# Run this to verify everything is ready
echo "MySQL: $(brew services list | grep mysql | awk '{print $2}')"
echo "Tomcat: $(ps aux | grep catalina | grep -v grep > /dev/null && echo 'Running' || echo 'Not running')"
echo "Application: $(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/pos-web/login)"
echo "Database: $(mysql -u root -p -e 'USE pos_db; SELECT COUNT(*) FROM users;' 2>/dev/null | tail -1) users"
```

All should show "started" / "Running" / "200" / "4 users".
