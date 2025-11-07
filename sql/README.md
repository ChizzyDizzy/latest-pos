# Database Setup and Maintenance Scripts

This directory contains SQL scripts for setting up and maintaining the SYOS POS System database.

## Quick Start

### For New Installations

If you're setting up the database for the first time:

```bash
mysql -u root -p < create_database.sql
```

This will create:
- Database: `syos_db`
- All required tables
- Default users (admin, cashier1, manager1, customer1, test)
- Sample inventory items
- Useful views and stored procedures

### For Existing Installations (Add Customer Role Support)

If you already have the database but need to add CUSTOMER role support:

```bash
mysql -u root -p syos_db < add_customer_role_support.sql
```

This will:
- Update the users table to support CUSTOMER role
- Add a default customer1 user if it doesn't exist
- Verify all changes

## Default User Credentials

After running the setup scripts, you can login with:

| Username   | Password     | Role     | Email                  |
|------------|--------------|----------|------------------------|
| admin      | admin123     | ADMIN    | admin@syos.com         |
| cashier1   | cashier123   | CASHIER  | cashier1@syos.com      |
| manager1   | manager123   | MANAGER  | manager1@syos.com      |
| customer1  | customer123  | CUSTOMER | customer1@syos.com     |
| test       | test123      | ADMIN    | test@syos.com          |

**IMPORTANT**: Change these default passwords in production!

## User Role Permissions

### ADMIN
- Full system access
- Manage users (create, update, delete)
- Access all reports
- Manage inventory
- Process sales

### MANAGER
- View all reports
- Manage inventory
- Process sales
- Cannot manage users

### CASHIER
- Process sales
- View bills
- Limited inventory access

### CUSTOMER
- Browse products (webapp)
- View own purchase history
- Limited access

## File Descriptions

### `create_database.sql`
Complete database setup script. Creates all tables, views, stored procedures, and inserts default data.
- Run for: New installations
- Creates: Full database structure with sample data

### `add_customer_role_support.sql`
Adds CUSTOMER role support to existing databases.
- Run for: Existing installations that need CUSTOMER role
- Modifies: users table role ENUM
- Adds: customer1 default user

### `seed_data.sql`
Sample data for users and items (legacy).
- Note: This is now integrated into create_database.sql

### `missing-column-user.sql`
Adds missing columns to users table if needed.
- Run if: You're upgrading from an older schema version

### `user-passwords.sql`
Contains user password information and hashes.
- For reference only

## Database Schema Overview

### Main Tables
1. **users** - System users with authentication
2. **items** - Inventory items with state tracking
3. **bills** - Sales transactions
4. **bill_items** - Line items for each bill
5. **stock_movements** - Inventory movement history
6. **audit_log** - System audit trail

### Useful Views
- `daily_sales_summary` - Daily sales metrics
- `low_stock_items` - Items below reorder level (< 50)
- `expiring_soon` - Items expiring within 7 days
- `inventory_value` - Total inventory value by state

### Stored Procedures
- `sp_record_sale()` - Record a sale transaction
- `sp_expire_items()` - Mark expired items

## Troubleshooting

### Cannot Login with Existing Users

If you can only login with admin but not other users:

1. Check if users exist:
```sql
USE syos_db;
SELECT username, email, role FROM users;
```

2. Reset passwords for all users:
```bash
mysql -u root -p syos_db < user-passwords.sql
```

3. Verify role ENUM includes all roles:
```sql
SHOW COLUMNS FROM users LIKE 'role';
```

### Customer Role Not Available

Run the customer role support script:
```bash
mysql -u root -p syos_db < add_customer_role_support.sql
```

### Database Connection Issues

1. Check MySQL is running:
```bash
sudo systemctl status mysql
```

2. Verify connection settings in `pos-core/src/main/resources/config/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/syos_db
db.username=root
db.password=your_password
```

### Reset Everything

To completely reset the database:
```bash
mysql -u root -p -e "DROP DATABASE IF EXISTS syos_db;"
mysql -u root -p < create_database.sql
```

## Password Hashing

The system uses SHA-256 for password hashing. To generate a new password hash:

```java
String password = "your_password";
String hash = MessageDigest.getInstance("SHA-256")
    .digest(password.getBytes())
    .toString(); // Convert to hex string
```

Or use online SHA-256 generators (not recommended for production).

## Maintenance

### Daily Tasks
- Run `sp_expire_items()` to mark expired items:
```sql
USE syos_db;
CALL sp_expire_items();
```

### Weekly Tasks
- Check low stock: `SELECT * FROM low_stock_items;`
- Check expiring items: `SELECT * FROM expiring_soon;`
- Review sales: `SELECT * FROM daily_sales_summary;`

### Backup
```bash
mysqldump -u root -p syos_db > backup_$(date +%Y%m%d).sql
```

### Restore
```bash
mysql -u root -p syos_db < backup_YYYYMMDD.sql
```

## Support

For issues or questions:
1. Check the main README.md in the project root
2. Review the database logs
3. Check application logs in the logs directory
