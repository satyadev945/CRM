# PostgreSQL Migration Guide

## Overview
This document describes the migration of the CRM application from MySQL to PostgreSQL database.

## Migration Date
Completed: 2024

## Changes Made

### 1. Database Driver Update (pom.xml)
**Changed:**
- Removed: `mysql-connector-j` (version 8.2.0)
- Added: `postgresql` (version 42.7.1)

**Location:** `/pom.xml`

### 2. Database Configuration (application.properties)
**Changed:**
- Connection URL: `jdbc:mysql://localhost:3306/crm` → `jdbc:postgresql://localhost:5432/crm`
- Driver class: Added `org.postgresql.Driver`
- Database platform: Added `org.hibernate.dialect.PostgreSQLDialect`
- Username: `root` → `postgres`
- Added PostgreSQL-specific JPA properties

**Location:** `/src/main/resources/application.properties`

### 3. Data Initialization Script (data.sql)
**Changed:**
- Removed MySQL-specific `SET FOREIGN_KEY_CHECKS=0/1` statements
- Updated date format: `'2018-02-24 00:00:00'` → `'2018-02-24'::date`
- Added PostgreSQL-compatible comments (-- instead of #)

**Location:** `/src/main/resources/data.sql`

### 4. Entity Classes - ID Generation Strategy
**Changed:** All entity classes updated from `GenerationType.AUTO` to `GenerationType.IDENTITY`

**Files Modified:**
- `/src/main/java/crm/entity/Customer.java`
- `/src/main/java/crm/entity/Contract.java`
- `/src/main/java/crm/entity/User.java`
- `/src/main/java/crm/entity/Role.java`
- `/src/main/java/crm/entity/Category.java`
- `/src/main/java/crm/entity/Pdf.java`

**Reason:** PostgreSQL works better with IDENTITY strategy for auto-incrementing primary keys.

### 5. Repository Native Query Update
**Changed:**
- Query: `select max(id) from crm.customer` → `SELECT MAX(id) FROM customer`
- Removed schema prefix (PostgreSQL uses 'public' schema by default)

**Location:** `/src/main/java/crm/repository/CustomerRepository.java`

## Database Setup Instructions

### Prerequisites
1. Install PostgreSQL 16 or later
2. Create database: `CREATE DATABASE crm;`
3. Create user (if needed): `CREATE USER postgres WITH PASSWORD 'password';`
4. Grant privileges: `GRANT ALL PRIVILEGES ON DATABASE crm TO postgres;`

### Configuration
Update the following in `application.properties` if your PostgreSQL setup differs:
- `spring.datasource.url` - Change host/port if not using localhost:5432
- `spring.datasource.username` - Change if not using 'postgres'
- `spring.datasource.password` - Update with your PostgreSQL password

### Running the Application
1. Ensure PostgreSQL is running
2. Clean and rebuild: `mvn clean install`
3. Run the application: `mvn spring-boot:run`
4. The application will automatically create tables and load initial data from `data.sql`

## Key Differences: MySQL vs PostgreSQL

### 1. ID Generation
- **MySQL:** AUTO strategy works well
- **PostgreSQL:** IDENTITY strategy is preferred (uses SERIAL/BIGSERIAL)

### 2. Schema Handling
- **MySQL:** Explicit schema names in queries (e.g., `crm.customer`)
- **PostgreSQL:** Uses 'public' schema by default, explicit schema not required

### 3. Date/Time Handling
- **MySQL:** Accepts various date formats including `'YYYY-MM-DD HH:MM:SS'`
- **PostgreSQL:** Stricter type casting, use `'YYYY-MM-DD'::date` for dates

### 4. Foreign Key Checks
- **MySQL:** Can disable with `SET FOREIGN_KEY_CHECKS=0`
- **PostgreSQL:** No equivalent, constraints are always enforced (can defer within transactions)

### 5. Case Sensitivity
- **MySQL:** Case-insensitive by default (depends on collation)
- **PostgreSQL:** Case-sensitive for identifiers unless quoted

## Testing Checklist

- [ ] Application starts successfully
- [ ] Database tables are created
- [ ] Initial data is loaded (4 users, 3 categories, 3 customers, 1 contract)
- [ ] User authentication works
- [ ] CRUD operations on customers work
- [ ] CRUD operations on contracts work
- [ ] Search functionality works
- [ ] Export to CSV/Excel/PDF works
- [ ] All repository queries return correct results

## Rollback Instructions

If you need to rollback to MySQL:
1. Restore the original `pom.xml` with MySQL connector
2. Restore the original `application.properties` with MySQL configuration
3. Restore the original `data.sql` with MySQL syntax
4. Revert entity classes to use `GenerationType.AUTO`
5. Restore the original CustomerRepository query with schema prefix
6. Run `mvn clean install`

## Support

For issues or questions regarding this migration, please contact the development team.

## Version History

- **v1.0** - Initial migration from MySQL to PostgreSQL
  - Spring Boot 3.2.0
  - PostgreSQL Driver 42.7.1
  - Java 17
