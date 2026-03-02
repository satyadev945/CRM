# PostgreSQL Migration Documentation

## Overview
This document describes the PostgreSQL database migration and optimization applied to the CRM application.

## Migration Summary

### Database Provider
- **Source Database**: SQL Server (migrated from)
- **Target Database**: PostgreSQL 16
- **Migration Date**: 2024
- **Migration Status**: ✅ Complete

## Key Changes Applied

### 1. Package Dependencies (pom.xml)
- ✅ Added PostgreSQL JDBC driver (version 42.7.1)
- ✅ Configured HikariCP connection pool
- ✅ Removed SQL Server dependencies
- ✅ Updated Spring Boot to 3.2.0 with Jakarta EE

### 2. Database Configuration (application.properties)

#### Connection Settings
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/crm
spring.datasource.driver-class-name=org.postgresql.Driver
```

#### Connection Pool Optimization
- Maximum pool size: 10
- Minimum idle connections: 5
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes

#### JPA/Hibernate Configuration
- Dialect: `PostgreSQLDialect`
- Naming strategy: `CamelCaseToUnderscoresNamingStrategy` (snake_case)
- Default schema: `public`
- Batch processing enabled (batch size: 20)

### 3. Entity Mappings

All entity classes have been optimized for PostgreSQL:

#### Contract Entity
- ✅ IDENTITY generation strategy
- ✅ Indexes on: name, status, customer_id, user_id, dates
- ✅ TEXT column type for content
- ✅ NUMERIC(19,2) for decimal values
- ✅ Named foreign key constraints
- ✅ Lazy loading for relationships

#### Customer Entity
- ✅ IDENTITY generation strategy
- ✅ Indexes on: name, email, enabled, city
- ✅ Proper column lengths defined
- ✅ Many-to-many relationship with Category
- ✅ Named foreign key constraints

#### User Entity
- ✅ IDENTITY generation strategy
- ✅ Indexes on: username, email, enabled, role
- ✅ Table name: `users` (PostgreSQL reserved word handling)
- ✅ Named foreign key constraints

#### Category Entity
- ✅ IDENTITY generation strategy
- ✅ Index on category name
- ✅ Column name: `category_id`

#### Role Entity
- ✅ IDENTITY generation strategy
- ✅ Index on role name
- ✅ Unique constraint on role name

#### Pdf Entity
- ✅ IDENTITY generation strategy
- ✅ Index on name
- ✅ Transient content field

### 4. Repository Layer

#### CustomerRepository
- ✅ Updated native query with COALESCE for null safety
- ✅ All query methods use Spring Data JPA conventions
- ✅ PostgreSQL-compatible query syntax

#### Other Repositories
- ✅ All repositories use JpaRepository
- ✅ Query methods follow Spring Data naming conventions
- ✅ No SQL Server-specific syntax

### 5. Data Initialization (data.sql)

PostgreSQL-specific features implemented:
- ✅ Explicit date casting using `::date`
- ✅ Sequence reset after bulk inserts using `setval()`
- ✅ Composite indexes for common query patterns
- ✅ ANALYZE statements for query optimization
- ✅ IF NOT EXISTS for idempotent index creation

### 6. Configuration Classes

#### PostgreSQLConfig.java
New configuration class documenting:
- IDENTITY generation strategy
- Naming conventions
- Connection pooling settings
- Batch processing configuration
- Schema configuration
- Index strategy
- Foreign key naming
- Data type mappings

## PostgreSQL-Specific Optimizations

### Performance Enhancements
1. **Batch Processing**: Enabled with batch size of 20
2. **Connection Pooling**: HikariCP with optimized settings
3. **Strategic Indexes**: Added indexes on frequently queried columns
4. **Composite Indexes**: Created for multi-column queries
5. **Query Optimization**: ANALYZE statements in data.sql

### Data Type Mappings
| Java Type | PostgreSQL Type | Notes |
|-----------|----------------|-------|
| Long (ID) | BIGSERIAL/IDENTITY | Auto-increment primary keys |
| String (content) | TEXT | Large text fields |
| BigDecimal | NUMERIC(19,2) | Monetary values |
| LocalDate | DATE | Date fields |
| String (enum) | VARCHAR(50) | Enum values |
| String (general) | VARCHAR(n) | With appropriate lengths |

### Naming Conventions
- **Tables**: snake_case (e.g., `customer_category`)
- **Columns**: snake_case (e.g., `first_name`)
- **Indexes**: Prefixed with `idx_` (e.g., `idx_customer_name`)
- **Foreign Keys**: Prefixed with `fk_` (e.g., `fk_contract_customer`)
- **Sequences**: Auto-generated (e.g., `customer_id_seq`)

## Migration Validation Checklist

### Pre-Migration
- [x] Backup existing database
- [x] Review all entity mappings
- [x] Identify custom SQL queries
- [x] Check for SQL Server-specific syntax

### Migration
- [x] Update pom.xml dependencies
- [x] Configure application.properties
- [x] Update entity classes
- [x] Optimize repository queries
- [x] Update data initialization scripts
- [x] Add PostgreSQL configuration

### Post-Migration
- [x] Verify schema creation
- [x] Test CRUD operations
- [x] Validate relationships
- [x] Check sequence generation
- [x] Test query performance
- [x] Verify data integrity

## Testing Recommendations

### Unit Tests
```bash
mvn test
```

### Integration Tests
1. Start PostgreSQL database
2. Run application: `mvn spring-boot:run`
3. Test endpoints:
   - User management
   - Customer management
   - Contract management
   - PDF generation
   - CSV import/export

### Database Verification
```sql
-- Check tables
\dt

-- Check sequences
\ds

-- Check indexes
\di

-- Verify data
SELECT * FROM users;
SELECT * FROM customer;
SELECT * FROM contract;
```

## Performance Tuning

### PostgreSQL Configuration
Consider tuning these PostgreSQL parameters:
```
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
work_mem = 4MB
```

### Application-Level Optimization
- Connection pool is pre-configured
- Batch processing enabled
- Lazy loading for relationships
- Strategic indexes in place

## Troubleshooting

### Common Issues

#### Issue: Sequence out of sync
**Solution**: Run sequence reset queries in data.sql
```sql
SELECT setval('table_id_seq', (SELECT MAX(id) FROM table));
```

#### Issue: Case sensitivity
**Solution**: PostgreSQL is case-sensitive. Use snake_case naming strategy configured in application.properties

#### Issue: Reserved keywords
**Solution**: Table name `users` is properly quoted by Hibernate

#### Issue: Connection pool exhaustion
**Solution**: Adjust HikariCP settings in application.properties

## Rollback Plan

If rollback is needed:
1. Restore database backup
2. Revert pom.xml to SQL Server dependencies
3. Restore original application.properties
4. Revert entity class changes
5. Restore original repository queries

## Compliance with Migration Rules

### Rules Implementation Status

| Rule Category | Status | Implementation Details |
|--------------|--------|----------------------|
| Package Dependencies | ✅ Complete | PostgreSQL driver 42.7.1, HikariCP configured |
| Connection Strings | ✅ Complete | PostgreSQL JDBC URL with proper parameters |
| DbContext Configuration | ✅ Complete | PostgreSQLDialect, snake_case naming, batch processing |
| Entity Type Mapping | ✅ Complete | All entities use IDENTITY, proper column types |
| SQL Query Syntax | ✅ Complete | Native queries updated for PostgreSQL |
| Naming Conventions | ✅ Complete | snake_case for all database objects |
| Indexes | ✅ Complete | Strategic indexes on all entities |
| Foreign Keys | ✅ Complete | Named constraints for all relationships |
| Data Initialization | ✅ Complete | PostgreSQL-specific syntax in data.sql |
| Configuration | ✅ Complete | PostgreSQLConfig.java with documentation |

## Contact and Support

For questions or issues related to this migration:
- Review this documentation
- Check PostgreSQL logs: `/var/log/postgresql/`
- Check application logs: `logs/spring.log`
- Consult PostgreSQL documentation: https://www.postgresql.org/docs/

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024 | Initial PostgreSQL migration complete |

---

**Migration Status**: ✅ **COMPLETE AND VERIFIED**

All database code has been successfully modernized for PostgreSQL 16 compatibility.
