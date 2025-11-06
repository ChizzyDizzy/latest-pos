# SYOS POS System - Multi-Module Web Application

## 🏗️ Architecture Overview

This is a **refactored multi-module Maven project** that separates business logic from presentation layers, enabling both **CLI** and **Web** interfaces to share the same core functionality.

### Multi-Module Structure

```
pos-system/
├── pom.xml                          # Parent POM (Java 17)
│
├── pos-core/                        # ⭐ Core Business Logic Library
│   ├── pom.xml
│   └── src/main/java/com/syos/
│       ├── domain/                  # Entities, Value Objects, Exceptions
│       ├── application/             # Services (Thread-Safe), DTOs
│       └── infrastructure/
│           ├── persistence/         # DAOs, Gateways, Connection Pool
│           ├── config/              # Database configuration
│           └── mappers/             # ResultSet mappers
│
├── pos-cli/                         # 💻 Command-Line Interface
│   ├── pom.xml
│   └── src/main/java/com/syos/cli/
│       ├── Main.java                # CLI entry point
│       ├── commands/                # CLI commands
│       ├── ui/                      # Menus, input readers
│       └── factories/               # CLI factories
│
└── pos-web/                         # 🌐 Web Application (MVC)
    ├── pom.xml
    ├── src/main/java/com/syos/web/
    │   ├── controllers/             # Servlets (MVC Controllers)
    │   │   ├── LoginServlet.java
    │   │   ├── DashboardServlet.java
    │   │   ├── SalesServlet.java
    │   │   ├── InventoryServlet.java
    │   │   ├── ReportsServlet.java
    │   │   └── LogoutServlet.java
    │   ├── filters/                 # Authentication, security filters
    │   ├── listeners/               # ServletContext listener
    │   └── utils/                   # JSON helpers
    └── src/main/webapp/
        ├── WEB-INF/
        │   ├── web.xml              # Deployment descriptor
        │   └── views/               # JSP files (MVC Views)
        │       ├── login.jsp
        │       ├── dashboard.jsp
        │       ├── common/          # Header/footer
        │       ├── sales/           # Sales views
        │       ├── inventory/       # Inventory views
        │       └── reports/         # Report views
        ├── css/                     # Stylesheets
        └── js/                      # JavaScript
```

## 🎯 Key Features

### ✅ Architectural Highlights

- **Multi-Module Maven Project**: Clean separation of concerns
- **MVC Pattern**: Servlets (Controllers), JSP (Views), Services (Model)
- **Thread-Safe Services**: Designed for concurrent access in Tomcat
- **Shared Business Logic**: Both CLI and Web use the same `pos-core`
- **Clean Architecture**: Domain → Application → Infrastructure layers
- **Design Patterns**: Factory, Singleton, Builder, State, Command, Gateway

### ✅ Concurrency & Thread Safety

- **Tomcat Thread Pool**: Configured for concurrent request handling
- **Synchronized Services**: Critical operations use locks to prevent race conditions
- **Connection Pooling**: Thread-safe database connection management (5-10 connections)
- **Session Isolation**: Each user's data isolated in HTTP sessions
- **Double-Check Locking**: Prevents inventory overselling during concurrent sales

### ✅ Web Application Features

- **User Authentication**: Role-based access (Admin, Manager, Cashier)
- **Dashboard**: Real-time metrics, alerts, recent transactions
- **Sales Management**: Create sales, view bills, concurrent transaction handling
- **Inventory Management**: Add stock, move to shelf, track expiry
- **Reports**: Daily sales, stock levels, reorder alerts
- **Security**: Authentication filters, security headers, session management

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 |
| Build Tool | Maven | 3.8+ |
| Web Container | Tomcat | 9 |
| Database | MySQL | 8.0+ |
| Frontend | JSP + JSTL | 2.3 / 1.2 |
| Servlet API | javax.servlet-api | 4.0.1 |
| JSON | Gson | 2.10.1 |
| Logging | SLF4J | 2.0.7 |
| Testing | JUnit 5 + Mockito | 5.10.0 |

## 📋 Prerequisites

Before building and running the application, ensure you have:

- **Java 17** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **MySQL 8.0+**
- **Apache Tomcat 9**

## 🗄️ Database Setup

### Step 1: Create Database and Schema

```bash
cd /home/user/syos-pos-system-chirath
mysql -u root -p < sql/create_database.sql
```

This script will:
- Create `syos_db` database
- Create tables: `users`, `items`, `bills`, `bill_items`, `stock_movements`, `audit_log`
- Insert demo users with hashed passwords
- Insert sample inventory items
- Create views and stored procedures

### Step 2: Verify Database Connection

Update database credentials if needed:
```properties
# pos-core/src/main/resources/config/application.properties
db.url=jdbc:mysql://localhost:3306/syos_db
db.username=root
db.password=SportS28
db.pool.initial=5
db.pool.max=10
```

### Demo User Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| manager1 | manager123 | MANAGER |
| cashier1 | cashier123 | CASHIER |

## 🏗️ Building the Project

### Build All Modules

From the project root directory:

```bash
# Clean and build all modules
mvn clean install

# Or build without running tests (faster)
mvn clean install -DskipTests
```

This will:
1. Build `pos-core` → Install to local Maven repository
2. Build `pos-cli` → Create executable JAR at `pos-cli/target/pos-cli.jar`
3. Build `pos-web` → Create WAR file at `pos-web/target/pos-web.war`

### Build Individual Modules

```bash
# Build only core
cd pos-core && mvn clean install

# Build only CLI
cd pos-cli && mvn clean package

# Build only web
cd pos-web && mvn clean package
```

## 🚀 Running the Applications

### Option 1: Run CLI Application

```bash
cd pos-cli
java -jar target/pos-cli.jar
```

Or use Maven:
```bash
cd pos-cli
mvn exec:java
```

### Option 2: Deploy Web Application to Tomcat

#### Method A: Copy WAR to Tomcat

```bash
# Copy WAR file to Tomcat webapps directory
cp pos-web/target/pos-web.war $CATALINA_HOME/webapps/

# Start Tomcat
$CATALINA_HOME/bin/startup.sh

# Access application at:
# http://localhost:8080/pos-web/
```

#### Method B: Use Maven Tomcat Plugin (for testing)

```bash
cd pos-web
mvn tomcat9:run

# Access application at:
# http://localhost:8080/pos/
```

### Option 3: Deploy to External Tomcat 9

1. **Copy WAR file**:
   ```bash
   cp pos-web/target/pos-web.war /path/to/tomcat9/webapps/
   ```

2. **Configure Tomcat Thread Pool** (Optional - `server.xml`):
   ```xml
   <Executor name="tomcatThreadPool"
             namePrefix="catalina-exec-"
             maxThreads="200"
             minSpareThreads="25"
             maxIdleTime="60000"/>

   <Connector executor="tomcatThreadPool"
              port="8080"
              protocol="HTTP/1.1"
              connectionTimeout="20000"
              redirectPort="8443" />
   ```

3. **Start Tomcat**:
   ```bash
   /path/to/tomcat9/bin/startup.sh
   ```

4. **Access Application**:
   ```
   http://localhost:8080/pos-web/
   ```

## 🔒 Security Configuration

### Production Deployment Checklist

- [ ] Change database credentials in `application.properties`
- [ ] Enable HTTPS in Tomcat (update `web.xml` cookie secure flag)
- [ ] Update password hashing algorithm if needed
- [ ] Configure firewall rules for database access
- [ ] Set appropriate file permissions
- [ ] Enable Tomcat access logs
- [ ] Configure session timeout (default: 30 minutes)

## 🧪 Testing

### Run Unit Tests

```bash
# Test all modules
mvn test

# Test specific module
cd pos-core && mvn test
```

### Test Web Application

1. Login with demo credentials
2. Test concurrent access by opening multiple browser tabs
3. Monitor Tomcat logs for thread safety:
   ```bash
   tail -f $CATALINA_HOME/logs/catalina.out
   ```

## 📊 Monitoring Concurrent Access

### Tomcat Thread Pool Monitoring

Check active threads and connections:

```bash
# View Tomcat Manager (if enabled)
http://localhost:8080/manager/status

# Or monitor via JConsole/VisualVM
jconsole
```

### Database Connection Pool Monitoring

The application logs connection pool statistics:

```log
INFO  DatabaseConnectionPool - ✓ Database connection pool initialized
INFO  DatabaseConnectionPool - Pool size: 5 connections ready
```

## 🐛 Troubleshooting

### Common Issues

**Issue: Port 8080 already in use**
```bash
# Find process using port 8080
lsof -i :8080
# Kill the process or change Tomcat port in server.xml
```

**Issue: Database connection failed**
```bash
# Check MySQL is running
sudo systemctl status mysql

# Test connection
mysql -u root -p syos_db
```

**Issue: WAR file not deploying**
```bash
# Check Tomcat logs
tail -f $CATALINA_HOME/logs/catalina.out

# Ensure Tomcat has write permissions to webapps directory
chmod 755 $CATALINA_HOME/webapps
```

**Issue: ClassNotFoundException**
```bash
# Rebuild all modules ensuring pos-core is installed
cd /home/user/syos-pos-system-chirath
mvn clean install
```

## 📈 Performance Tuning

### Tomcat Configuration

Edit `$CATALINA_HOME/conf/server.xml`:

```xml
<!-- Increase thread pool for high concurrency -->
<Executor name="tomcatThreadPool"
          maxThreads="500"
          minSpareThreads="50"/>

<!-- Increase max connections -->
<Connector port="8080"
           maxConnections="10000"
           acceptCount="100"/>
```

### Database Connection Pool

Edit `pos-core/src/main/resources/config/application.properties`:

```properties
# Increase pool size for high load
db.pool.initial=10
db.pool.max=50
```

## 📚 API Endpoints (Web Application)

### Authentication
- `GET/POST /login` - User login
- `GET /logout` - User logout

### Dashboard
- `GET /dashboard` - Main dashboard with metrics

### Sales
- `GET /sales/new` - New sale form
- `POST /sales/add-item` - Add item to sale (AJAX)
- `POST /sales/complete` - Complete and save sale
- `GET /sales/list` - List all bills
- `GET /sales/view/{billNumber}` - View specific bill

### Inventory
- `GET /inventory` - List all inventory items
- `GET /inventory/add` - Add stock form
- `POST /inventory/add` - Add new stock
- `POST /inventory/move-to-shelf` - Move items to shelf (AJAX)
- `GET /inventory/low-stock` - Low stock items
- `GET /inventory/expiring` - Expiring items

### Reports
- `GET /reports` - Reports menu
- `GET /reports/daily-sales?date=YYYY-MM-DD` - Daily sales report
- `GET /reports/stock` - Stock report
- `GET /reports/reorder` - Reorder report

## 🎓 Architecture Decisions

### Why Multi-Module?

- **Reusability**: Core business logic shared between CLI and Web
- **Maintainability**: Changes to business logic don't affect UI layers
- **Testability**: Each module can be tested independently
- **Scalability**: Easy to add new interfaces (e.g., mobile app, REST API)

### Why Thread-Safe Services?

- **Concurrent Users**: Multiple users can perform operations simultaneously
- **Data Integrity**: Prevents race conditions in inventory management
- **Scalability**: Leverages Tomcat's thread pool for performance

### Why MVC Pattern?

- **Separation of Concerns**: Controllers, Views, and Models are independent
- **Flexibility**: Easy to change frontend without touching business logic
- **Standard Practice**: Industry-standard web application architecture

## 📝 Development Workflow

### Adding New Features

1. **Add business logic to `pos-core`**:
   ```java
   // pos-core/src/main/java/com/syos/application/services/
   public class MyService {
       public synchronized void newFeature() { ... }
   }
   ```

2. **Add CLI command (if needed)**:
   ```java
   // pos-cli/src/main/java/com/syos/cli/commands/
   public class MyCommand implements Command { ... }
   ```

3. **Add Web controller and view**:
   ```java
   // pos-web/src/main/java/com/syos/web/controllers/
   @WebServlet("/my-feature")
   public class MyServlet extends HttpServlet { ... }

   // pos-web/src/main/webapp/WEB-INF/views/
   // my-feature.jsp
   ```

4. **Rebuild and test**:
   ```bash
   mvn clean install
   ```

## 🤝 Contributing

When contributing to this project:

1. Keep business logic in `pos-core`
2. Ensure services are thread-safe
3. Write unit tests for new features
4. Follow MVC pattern in web module
5. Update this README if adding major features

## 📄 License

Copyright © 2025 SYOS POS System

## 📞 Support

For issues or questions:
- Check logs in `$CATALINA_HOME/logs/`
- Review database connection in `application.properties`
- Ensure all modules are built successfully
- Verify Tomcat version is 9.x

---

**Happy POS-ing! 🚀**

*Multi-threaded, concurrent, scalable POS system with both CLI and Web interfaces.*
