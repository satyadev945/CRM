# PostgreSQL Migration Guide

## Overview
This application has been migrated from MySQL to PostgreSQL 16. This document outlines the changes made and setup instructions.

## Changes Made

### 1. Database Driver (pom.xml)
- **Removed**: MySQL Connector (`com.mysql:mysql-connector-j`)
- **Added**: PostgreSQL Driver (`org.postgresql:postgresql`)

### 2. Database Configuration (application.properties)
- **Connection URL**: Changed from `jdbc:mysql://localhost:3306/crm` to `jdbc:postgresql://localhost:5432/crm`
- **Driver Class**: Set to `org.postgresql.Driver`
- **Dialect**: Set to `org.hibernate.dialect.PostgreSQLDialect`
- **Username**: Changed from `root` to `postgres` (default PostgreSQL user)
- **Added**: `spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true` for PostgreSQL LOB handling

### 3. Entity Classes
Updated all entity classes to use `GenerationType.IDENTITY` instead of `GenerationType.AUTO`:
- Contract.java
- Customer.java
- User.java
- Category.java
- Role.java
- Pdf.java

**Reason**: PostgreSQL works best with IDENTITY strategy for auto-incrementing primary keys.

### 4. Data Initialization Script (data.sql)
- **Removed**: MySQL-specific `SET FOREIGN_KEY_CHECKS=0/1` statements
- **Updated**: String values in INSERT statements (removed quotes around numeric values)
- **Updated**: Timestamp format to use PostgreSQL cast syntax (`'2018-02-24 00:00:00'::timestamp`)
- **Added**: Sequence reset statements to ensure auto-increment continues from the last inserted ID

### 5. Repository Layer
Updated `CustomerRepository.java`:
- Changed native query from `select max(id) from crm.customer` to `SELECT MAX(id) FROM customer`
- **Reason**: PostgreSQL uses public schema by default, and schema qualification is not needed

## PostgreSQL Setup Instructions

### Prerequisites
1. Install PostgreSQL 16 (or compatible version)
2. Ensure PostgreSQL service is running

### Database Setup

```bash
# Connect to PostgreSQL as superuser
psql -U postgres

# Create the database
CREATE DATABASE crm;

# Create a user (optional, or use default postgres user)
CREATE USER crmuser WITH PASSWORD 'password';

# Grant privileges
GRANT ALL PRIVILEGES ON DATABASE crm TO crmuser;

# Exit psql
\q
```

### Application Configuration

Update `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/crm
spring.datasource.username=postgres
spring.datasource.password=your_password_here
```

### Running the Application

```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will:
1. Create all necessary tables (due to `spring.jpa.hibernate.ddl-auto=create-drop`)
2. Execute the `data.sql` script to populate initial data
3. Start the web server on the default port (8080)

## Key Differences: MySQL vs PostgreSQL

### 1. Case Sensitivity
- **MySQL**: Case-insensitive by default for table/column names
- **PostgreSQL**: Case-sensitive (converts unquoted identifiers to lowercase)
- **Impact**: Minimal, as we're using lowercase names

### 2. Auto-Increment
- **MySQL**: Uses `AUTO_INCREMENT`
- **PostgreSQL**: Uses `SERIAL` or `IDENTITY` columns with sequences
- **Impact**: Handled by JPA `GenerationType.IDENTITY`

### 3. Boolean Type
- **MySQL**: Uses TINYINT(1) for boolean
- **PostgreSQL**: Has native BOOLEAN type
- **Impact**: JPA handles this automatically

### 4. String Comparison
- **MySQL**: Case-insensitive by default (depends on collation)
- **PostgreSQL**: Case-sensitive by default
- **Impact**: Use `ILIKE` instead of `LIKE` for case-insensitive searches if needed

### 5. Date/Time Functions
- **MySQL**: Uses functions like `NOW()`, `CURDATE()`
- **PostgreSQL**: Uses `NOW()`, `CURRENT_DATE`, `CURRENT_TIMESTAMP`
- **Impact**: None in this application (using JPA/Hibernate)

## Testing Checklist

After migration, verify:
- [ ] Application starts without errors
- [ ] All tables are created correctly
- [ ] Initial data is loaded (4 users, 3 categories, 3 customers, 1 contract)
- [ ] CRUD operations work for all entities
- [ ] User authentication works
- [ ] File upload/download features work
- [ ] Export to CSV/Excel/PDF works
- [ ] Search and filter operations work

## Troubleshooting

### Connection Issues
```
Error: Connection refused
Solution: Ensure PostgreSQL is running and listening on port 5432
```

### Authentication Failed
```
Error: password authentication failed
Solution: Check username/password in application.properties
```

### Schema/Table Not Found
```
Error: relation "table_name" does not exist
Solution: Ensure spring.jpa.hibernate.ddl-auto is set to create-drop or update
```

### Sequence Issues
```
Error: duplicate key value violates unique constraint
Solution: Run the sequence reset statements in data.sql
```

## Rollback to MySQL

If you need to rollback to MySQL:

1. Restore the original `pom.xml` (use MySQL connector)
2. Restore the original `application.properties` (MySQL connection string)
3. Restore the original `data.sql` (with MySQL syntax)
4. Revert entity classes to use `GenerationType.AUTO`
5. Revert the native query in `CustomerRepository.java`

## Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Data JPA with PostgreSQL](https://spring.io/guides/gs/accessing-data-jpa/)
- [Hibernate PostgreSQL Dialect](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#database-dialect)
