# CLI Application Quick Fix Guide

## Issues Fixed

This guide addresses the following issues:
1. ✅ Corrupted `.idea/compiler.xml` file preventing CLI startup
2. ✅ CUSTOMER role not available in database
3. ✅ User login issues (only admin could login initially)

## Step 1: Fix Database (Add CUSTOMER Role Support)

The CUSTOMER role was missing from your database schema. Run this command:

### On Windows (Command Prompt or PowerShell):
```cmd
cd D:\11-3-project\final\latest-pos\sql
mysql -u root -p syos_db < add_customer_role_support.sql
```

### On Windows (Git Bash):
```bash
cd /d/11-3-project/final/latest-pos/sql
mysql -u root -p syos_db < add_customer_role_support.sql
```

### On Linux/Mac:
```bash
cd /path/to/latest-pos/sql
mysql -u root -p syos_db < add_customer_role_support.sql
```

**What this does:**
- Modifies the `users` table to support CUSTOMER role
- Creates a default customer1 user (password: customer123)
- Verifies all changes

## Step 2: Verify Database Changes

Login to MySQL and verify:

```sql
USE syos_db;

-- Check the role column now includes CUSTOMER
SHOW COLUMNS FROM users LIKE 'role';

-- Verify all users exist
SELECT username, email, role FROM users ORDER BY role;
```

You should see all 5 users:
- admin (ADMIN) - password: admin123
- test (ADMIN) - password: test123
- manager1 (MANAGER) - password: manager123
- cashier1 (CASHIER) - password: cashier123
- customer1 (CUSTOMER) - password: customer123

## Step 3: Fix IntelliJ IDEA Compiler Configuration

The `.idea/compiler.xml` file has been fixed (git merge conflicts removed).

### If you still see the error:

1. **Option A: Reload IntelliJ IDEA**
   - Close IntelliJ IDEA completely
   - Reopen the project
   - It should reload the fixed compiler.xml

2. **Option B: Invalidate Caches** (if option A doesn't work)
   - In IntelliJ IDEA: `File` → `Invalidate Caches`
   - Check "Invalidate and Restart"
   - Click OK

3. **Option C: Reimport Maven Project**
   - Right-click on `pom.xml` (root)
   - Select `Maven` → `Reload Project`

## Step 4: Build the Project

### Using Maven (Command Line):
```bash
cd D:\11-3-project\final\latest-pos
mvn clean package -DskipTests
```

### Using IntelliJ IDEA:
- Open Maven tool window (View → Tool Windows → Maven)
- Expand `Lifecycle`
- Double-click `clean`, then `package`

## Step 5: Run the CLI Application

### Option 1: From IntelliJ IDEA
1. Navigate to `pos-cli/src/main/java/com/syos/cli/Main.java`
2. Right-click on the file
3. Select `Run 'Main.main()'`

### Option 2: From Command Line
```bash
cd pos-cli/target
java -jar pos-cli-1.0-SNAPSHOT.jar
```

### Option 3: Using Maven
```bash
cd D:\11-3-project\final\latest-pos\pos-cli
mvn exec:java -Dexec.mainClass="com.syos.cli.Main"
```

## Step 6: Test User Login

After starting the CLI, test login with each user:

### Test Admin User
```
Username: admin
Password: admin123
```

### Test Manager User
```
Username: manager1
Password: manager123
```

### Test Cashier User
```
Username: cashier1
Password: cashier123
```

### Test Customer User (NEW!)
```
Username: customer1
Password: customer123
```

## Troubleshooting

### Issue: Still can't login with non-admin users

**Solution**: The passwords might be incorrect in the database. Run:

```bash
mysql -u root -p syos_db < sql/user-passwords.sql
```

This will reset all user passwords to the defaults listed above.

### Issue: Compiler.xml error persists

**Solution**: The file might be locked by IntelliJ.

1. Close IntelliJ IDEA completely
2. Delete `.idea/compiler.xml`
3. Reopen IntelliJ IDEA (it will regenerate the file)

### Issue: Database connection errors in CLI

**Solution**: Check database configuration:

1. Open `pos-core/src/main/resources/config/database.properties`
2. Verify settings:
```properties
db.url=jdbc:mysql://localhost:3306/syos_db
db.username=root
db.password=your_mysql_password
```

### Issue: "customer1 already exists" when running SQL

This is normal if you previously created a customer1 user manually. The script safely handles this.

### Issue: CLI shows "package does not exist" errors

The package declaration issues have been fixed. Make sure to:
1. Pull the latest changes: `git pull`
2. Rebuild: `mvn clean package`

## Database Credentials Reference

Keep these handy for testing:

| Username   | Password     | Role     |
|------------|--------------|----------|
| admin      | admin123     | ADMIN    |
| cashier1   | cashier123   | CASHIER  |
| manager1   | manager123   | MANAGER  |
| customer1  | customer123  | CUSTOMER |
| test       | test123      | ADMIN    |

## What Each Role Can Do

### ADMIN
- ✅ Create/manage users (including CUSTOMER)
- ✅ Full access to reports
- ✅ Manage inventory
- ✅ Process sales
- ✅ View all data

### MANAGER
- ✅ View reports
- ✅ Manage inventory
- ✅ Process sales
- ❌ Cannot create users

### CASHIER
- ✅ Process sales
- ✅ View bills
- ❌ Limited inventory access
- ❌ No user management

### CUSTOMER
- ✅ Browse products (webapp)
- ✅ View purchase history
- ❌ Cannot access admin features

## Files Changed

The following files have been fixed/created:

1. ✅ `.idea/compiler.xml` - Fixed git merge conflicts
2. ✅ `sql/create_database.sql` - Added CUSTOMER role support
3. ✅ `sql/add_customer_role_support.sql` - New migration script
4. ✅ `sql/README.md` - Comprehensive SQL documentation
5. ✅ All CLI command files - Fixed package declarations

## Need More Help?

1. Check `sql/README.md` for detailed database documentation
2. Check application logs in the console
3. Verify MySQL is running: `mysql -u root -p -e "SELECT 1"`
4. Check the main README.md for overall project documentation

## Success Checklist

- [ ] Database updated with CUSTOMER role support
- [ ] All 5 users visible in database
- [ ] IntelliJ IDEA loads project without compiler.xml error
- [ ] Maven build succeeds: `mvn clean package`
- [ ] CLI application starts without errors
- [ ] Can login with admin credentials
- [ ] Can login with other user credentials
- [ ] Can register new CUSTOMER users from admin account
- [ ] Web application works with all roles

Once all items are checked, your system is fully operational!
