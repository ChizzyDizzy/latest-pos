# SYOS POS System - Complete Setup Guide

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Default Login Credentials](#default-login-credentials)
- [Project Structure](#project-structure)
- [Usage Guide](#usage-guide)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Design Patterns](#design-patterns)

---

## Overview

SYOS POS System is a comprehensive Point of Sale (POS) application for Synex Outlet Store (SYOS), demonstrating clean code principles, SOLID design principles, and advanced software architecture patterns. The system supports both **Command-Line Interface (CLI)** and **Web Application** interfaces, sharing the same core business logic.

### Key Objectives
- **Eliminate Manual Errors**: Automated calculations and inventory tracking
- **Improve Efficiency**: Reduce customer wait times during peak hours
- **Real-time Inventory**: Automatic stock updates with every transaction
- **Comprehensive Reporting**: Daily sales, stock levels, and reorder alerts
- **Clean Architecture**: Maintainable, testable, and extensible codebase
- **Multi-User Support**: Concurrent access with thread-safe operations

---

## Features

### Sales Management
- Fast checkout process with automated price calculations
- Cash payment processing with change calculation
- Bill generation with detailed itemization
- Transaction history and retrieval
- Concurrent transaction handling for multiple users

### Inventory Control
- Real-time stock tracking
- Automated reorder level alerts (< 50 items)
- Expiry date monitoring with 7-day warnings
- Stock movement tracking (Store → Shelf → Sold)
- Batch-wise inventory management
- State-based item lifecycle management

### Reporting Suite
- Daily sales reports with revenue analysis
- Current stock reports by category/state
- Low stock alerts and reorder suggestions
- Expiring items report with urgency levels
- Export reports to file system

### User Management
- Role-based access control (Admin, Manager, Cashier, Customer)
- Secure authentication with SHA-256 password hashing
- User activity logging
- Session management with 30-minute timeout
- Multi-user concurrent access support

---

## Architecture

The system implements **Clean Architecture (Onion Architecture)** with clear separation of concerns across three main modules:

```
┌──────────────────────────────────────────────┐
│            Presentation Layer                │
│     (CLI Application / Web Servlets)         │
├──────────────────────────────────────────────┤
│          Infrastructure Layer                │
│   (Database, Factories, Connection Pool)     │
├──────────────────────────────────────────────┤
│           Application Layer                  │
│      (Services, Commands, Reports)           │
├──────────────────────────────────────────────┤
│             Domain Layer                     │
│   (Entities, Value Objects, Interfaces)      │
└──────────────────────────────────────────────┘
```

### Multi-Module Structure

```
pos-system/
├── pos-core/          ⭐ Core Business Logic Library (Thread-Safe)
├── pos-cli/           💻 Command-Line Interface
└── pos-web/           🌐 Web Application (MVC Pattern)
```

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Extensible through abstractions, not modifications
- **Liskov Substitution**: Implementations are interchangeable
- **Interface Segregation**: Focused, cohesive interfaces
- **Dependency Inversion**: Depend on abstractions, not concretions

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 17+ |
| **Build Tool** | Maven | 3.8+ |
| **Database** | MySQL | 8.0+ |
| **Web Container** | Apache Tomcat | 9 |
| **Web Framework** | Jakarta Servlets | 4.0.1 |
| **Frontend** | JSP + JSTL | 2.3 / 1.2 |
| **JSON Processing** | Gson | 2.10.1 |
| **Logging** | SLF4J | 2.0.7 |
| **Testing** | JUnit 5 + Mockito | 5.10.0 / 5.7.0 |

---

## Prerequisites

Before installing and running the application, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   java -version
   # Should show version 17 or higher
   ```

2. **Apache Maven 3.8 or higher**
   ```bash
   mvn -version
   # Should show version 3.8 or higher
   ```

3. **MySQL Server 8.0 or higher**
   ```bash
   mysql --version
   # Should show version 8.0 or higher
   ```

4. **Apache Tomcat 9** (for Web Application only)
   - Download from: https://tomcat.apache.org/download-90.cgi
   - Set `CATALINA_HOME` environment variable

---

## Installation & Setup

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd latest-pos
```

### Step 2: Setup Database

Run the database setup script to create the database, tables, and seed initial data:

```bash
mysql -u root -p < sql/create_database.sql
```

**What this does:**
- Creates `syos_db` database
- Creates all required tables (users, items, bills, bill_items, stock_movements, audit_log)
- Inserts default users with hashed passwords
- Inserts sample inventory items
- Creates database views for reporting
- Creates stored procedures for common operations

### Step 3: Configure Database Connection

Update the database credentials in the configuration file if needed:

**File:** `pos-core/src/main/resources/config/application.properties`

```properties
db.url=jdbc:mysql://localhost:3306/syos_db
db.username=root
db.password=YOUR_MYSQL_PASSWORD
db.pool.initial=5
db.pool.max=10
```

### Step 4: Build the Project

Build all modules from the project root:

```bash
# Build all modules (recommended)
mvn clean install

# Or build without running tests (faster)
mvn clean install -DskipTests
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
[INFO] ------------------------------------------------------------------------
```

This will:
1. Build `pos-core` and install it to local Maven repository
2. Build `pos-cli` and create executable JAR at `pos-cli/target/pos-cli.jar`
3. Build `pos-web` and create WAR file at `pos-web/target/pos-web.war`

---

## Running the Application

You can run the application in two ways: **CLI Mode** or **Web Application Mode**.

### Option 1: Run CLI Application

```bash
cd pos-cli
java -jar target/pos-cli.jar
```

Or using Maven:
```bash
cd pos-cli
mvn exec:java
```

**Expected Output:**
```
Starting SYOS POS System - CLI Application...
Database connection pool initialized
=== MAIN MENU ===
1. Sales
2. Inventory
3. Reports
4. User Management
5. Logout
Enter your choice:
```

### Option 2: Run Web Application

#### Method A: Deploy to Tomcat (Recommended for Production)

1. **Copy WAR file to Tomcat webapps directory:**
   ```bash
   cp pos-web/target/pos-web.war $CATALINA_HOME/webapps/
   ```

2. **Start Tomcat:**
   ```bash
   $CATALINA_HOME/bin/startup.sh
   ```

3. **Access the application:**
   ```
   http://localhost:8080/pos-web/
   ```

#### Method B: Use Maven Tomcat Plugin (for Development/Testing)

```bash
cd pos-web
mvn tomcat9:run
```

**Access the application:**
```
http://localhost:8080/pos/
```

**Expected Console Output:**
```
INFO  AppContextListener - === SYOS POS Web Application Starting ===
INFO  AppContextListener - ✓ Database connection pool initialized
INFO  AppContextListener - === Application Ready for Concurrent Access ===
```

---

## Default Login Credentials

The system comes with pre-configured user accounts for testing and demonstration:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| **admin** | **admin123** | ADMIN | Full system access, user management |
| **manager_new** | **123456** | MANAGER | Sales, inventory, and reports |
| **cashier_new** | **123456** | CASHIER | Sales and basic inventory viewing |
| **customer_new** | **123456** | CUSTOMER | Browse products and view bills |

**Security Note:** Change these passwords in production environments!

---

## Project Structure

```
latest-pos/
├── pom.xml                          # Parent POM (Java 17)
│
├── pos-core/                        # ⭐ Core Business Logic Library
│   ├── pom.xml
│   └── src/main/java/com/syos/
│       ├── domain/                  # Business entities and logic
│       │   ├── entities/            # Bill, Item, User, BillItem
│       │   ├── valueobjects/        # Money, Quantity, ItemCode, UserId
│       │   ├── decorators/          # OnlineTransactionDecorator
│       │   ├── exceptions/          # Business exceptions
│       │   └── interfaces/          # BillInterface, ItemState
│       │
│       ├── application/             # Thread-safe services
│       │   ├── services/            # SalesService, InventoryService, etc.
│       │   ├── reports/             # Report generators
│       │   ├── visitors/            # Bill processing visitors
│       │   └── interfaces/          # Command interface
│       │
│       ├── infrastructure/          # Technical infrastructure
│       │   ├── persistence/
│       │   │   ├── connection/      # DatabaseConnectionPool
│       │   │   ├── gateways/        # Data access gateways
│       │   │   └── mappers/         # ResultSet mappers
│       │   ├── factories/           # ServiceFactory
│       │   └── config/              # DatabaseConfig
│       │
│       └── shared/                  # Cross-cutting concerns
│           └── utils/               # PasswordHashGenerator, etc.
│
├── pos-cli/                         # 💻 CLI Application
│   ├── pom.xml
│   └── src/main/java/com/syos/cli/
│       ├── Main.java                # CLI entry point
│       ├── commands/                # Command pattern implementations
│       │   ├── sales/               # CreateSaleCommand, ViewBillsCommand
│       │   ├── inventory/           # AddStockCommand, MoveToShelfCommand
│       │   ├── reports/             # Report generation commands
│       │   └── user/                # User management commands
│       ├── ui/
│       │   ├── cli/                 # CLIApplication, InputReader
│       │   ├── menu/                # Composite pattern menu system
│       │   └── presenters/          # Output presenters
│       └── factories/               # CLI factories
│
├── pos-web/                         # 🌐 Web Application (MVC)
│   ├── pom.xml
│   ├── src/main/java/com/syos/web/
│   │   ├── controllers/             # Servlets (MVC Controllers)
│   │   │   ├── LoginServlet.java
│   │   │   ├── DashboardServlet.java
│   │   │   ├── SalesServlet.java
│   │   │   ├── InventoryServlet.java
│   │   │   ├── ReportsServlet.java
│   │   │   ├── ProductsServlet.java
│   │   │   ├── UsersServlet.java
│   │   │   └── LogoutServlet.java
│   │   ├── filters/                 # Authentication & security
│   │   ├── listeners/               # ServletContext listeners
│   │   └── utils/                   # JSON helpers
│   │
│   └── src/main/webapp/
│       ├── WEB-INF/
│       │   ├── web.xml              # Deployment descriptor
│       │   └── views/               # JSP files (MVC Views)
│       │       ├── login.jsp
│       │       ├── dashboard.jsp
│       │       ├── common/          # Header/footer templates
│       │       ├── sales/           # Sales-related views
│       │       ├── inventory/       # Inventory views
│       │       └── reports/         # Report views
│       ├── css/                     # Stylesheets
│       └── js/                      # JavaScript files
│
├── sql/                             # Database scripts
│   └── create_database.sql          # Complete database setup
│
├── testing/                         # Test files and documentation
│   ├── jmeter/                      # JMeter test plans
│   ├── postman/                     # Postman collections
│   └── junit/                       # JUnit test cases
│
├── reports/                         # Generated report files
├── docs/                            # Additional documentation
├── README.md                        # This file
└── guide_testing.md                 # Testing guide
```

---

## Usage Guide

### Using the Web Application

1. **Login**
   - Navigate to `http://localhost:8080/pos-web/`
   - Enter username and password from the credentials table
   - Click "Login"

2. **Dashboard**
   - View real-time metrics (total sales, low stock alerts, expiring items)
   - Quick access to all modules
   - Recent transactions summary

3. **Process a Sale**
   - Click "Sales" → "Create New Sale"
   - Add items using item codes (e.g., MILK001, BREAD001)
   - Enter quantities
   - Enter cash tendered
   - System calculates total and change
   - Complete sale to generate bill

4. **Manage Inventory**
   - Click "Inventory" → "Add Stock"
   - Enter item details (code, name, price, quantity, expiry date)
   - Move items from store to shelf when ready
   - View low stock and expiring items alerts

5. **Generate Reports**
   - Click "Reports"
   - Choose report type:
     - Daily Sales Report (specify date)
     - Stock Report (current inventory)
     - Reorder Report (low stock items)
   - View and export reports

### Using the CLI Application

1. **Login**
   ```
   Enter username: admin
   Enter password: admin123
   ```

2. **Navigate Menus**
   - Use number keys to select menu options
   - Follow on-screen prompts

3. **Sample Workflow**
   ```
   1. Select "Sales" → "Create New Sale"
   2. Add items: MILK001 (qty: 2), BREAD001 (qty: 1)
   3. Enter cash payment: $10.00
   4. System prints bill and calculates change
   5. Stock automatically updated
   ```

---

## Testing

The system includes comprehensive testing resources. For detailed testing instructions, see **[guide_testing.md](guide_testing.md)**.

### Testing Resources Included:

1. **JMeter Test Plan**: Load testing with 200 concurrent users
   - Location: `testing/jmeter/pos-system-load-test.jmx`

2. **Postman Collection**: API endpoint testing
   - Location: `testing/postman/SYOS-POS-API-Tests.json`

3. **JUnit/Mockito Tests**: Servlet unit tests
   - Location: `pos-web/src/test/java/com/syos/web/controllers/`

### Quick Test Commands

```bash
# Run all unit tests
mvn test

# Run tests for specific module
cd pos-core && mvn test
cd pos-web && mvn test

# Build and test everything
mvn clean install
```

### Testing Concurrent Access

1. Open 3 browser tabs
2. Login as different users in each tab:
   - Tab 1: `admin` / `admin123`
   - Tab 2: `manager_new` / `123456`
   - Tab 3: `cashier_new` / `123456`
3. Perform operations simultaneously (create sales, manage inventory)
4. Verify data integrity and thread safety

---

## Troubleshooting

### Build Failed?

```bash
# Check Java version (need 17+)
java -version

# Check Maven version (need 3.8+)
mvn -version

# Clean and rebuild
mvn clean install -U
```

### Database Connection Failed?

```bash
# Check MySQL is running
sudo systemctl status mysql

# Test connection
mysql -u root -p syos_db

# Verify credentials in:
# pos-core/src/main/resources/config/application.properties
```

### Port 8080 Already in Use?

```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change Tomcat port in server.xml
```

### WAR File Not Deploying?

```bash
# Check Tomcat logs
tail -f $CATALINA_HOME/logs/catalina.out

# Ensure write permissions
chmod 755 $CATALINA_HOME/webapps

# Rebuild and redeploy
mvn clean package
cp pos-web/target/pos-web.war $CATALINA_HOME/webapps/
```

### Login Not Working?

1. Verify database was created:
   ```bash
   mysql -u root -p -e "USE syos_db; SELECT username, role FROM users;"
   ```

2. Check password hashes are correct in database

3. Clear browser cookies and cache

4. Check Tomcat logs for authentication errors

---

## Design Patterns

The project implements 11 GoF design patterns:

1. **Command Pattern** - Encapsulates all user actions as commands
2. **Factory Method** - Centralizes object creation (CommandFactory, ServiceFactory)
3. **Singleton** - Database connection pool, input reader
4. **Object Pool** - Efficient database connection management
5. **Table Data Gateway** - Clean database access layer
6. **State Pattern** - Item lifecycle (InStore → OnShelf → Sold/Expired)
7. **Builder Pattern** - Complex object construction (Item, Bill)
8. **Template Method** - Standardized report generation
9. **Composite Pattern** - Hierarchical menu system
10. **Visitor Pattern** - Bill processing without modifying entities
11. **Decorator Pattern** - Enhanced bill functionality for online transactions

---

## Concurrency & Thread Safety

### Thread-Safe Features
- **Tomcat Thread Pool**: Handles 200+ concurrent requests
- **Synchronized Services**: Critical operations use locks to prevent race conditions
- **Connection Pooling**: Thread-safe database connection management (5-10 connections)
- **Session Isolation**: Each user's data isolated in HTTP sessions
- **Double-Check Locking**: Prevents inventory overselling during concurrent sales

### Performance Tuning

**Tomcat Configuration** (`$CATALINA_HOME/conf/server.xml`):
```xml
<Executor name="tomcatThreadPool"
          maxThreads="500"
          minSpareThreads="50"/>
```

**Database Connection Pool** (`application.properties`):
```properties
db.pool.initial=10
db.pool.max=50
```

---

## Security Features

- **SHA-256 Password Hashing**: Secure password storage
- **Session Management**: 30-minute timeout with HTTP-only cookies
- **Authentication Filter**: Protects all secured resources
- **Role-Based Access Control**: Four user roles with different permissions
- **SQL Injection Prevention**: Prepared statements throughout
- **XSS Prevention**: Output escaping in JSP views

---

## Additional Documentation

- **guide_testing.md** - Comprehensive testing guide with instructions for JMeter, Postman, and JUnit tests
- **SQL Scripts** - Database schema and seed data in `sql/create_database.sql`

---

## Support

For issues, questions, or contributions:
1. Check application logs in `$CATALINA_HOME/logs/catalina.out`
2. Review database connection settings in `application.properties`
3. Ensure all prerequisites are installed with correct versions
4. Verify Tomcat version is 9.x

---

## License

Copyright © 2025 SYOS POS System

---

**Happy POS-ing! 🚀**

*A professional, multi-threaded, concurrent POS system with both CLI and Web interfaces, demonstrating clean architecture and enterprise-level design patterns.*
