package crm;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * PostgreSQL-specific configuration for the CRM application.
 * This configuration enables JPA repositories and transaction management
 * optimized for PostgreSQL database.
 */
@Configuration
@EnableJpaRepositories(basePackages = "crm.repository")
@EnableTransactionManagement
public class PostgreSQLConfig {

    /**
     * PostgreSQL-specific configuration notes:
     * 
     * 1. IDENTITY Generation Strategy:
     *    - All entities use GenerationType.IDENTITY which maps to PostgreSQL SERIAL/IDENTITY columns
     *    - This is the recommended approach for PostgreSQL 10+
     * 
     * 2. Naming Convention:
     *    - Using CamelCaseToUnderscoresNamingStrategy for snake_case (PostgreSQL standard)
     *    - Configured in application.properties
     * 
     * 3. Connection Pooling:
     *    - HikariCP is configured with PostgreSQL-optimized settings
     *    - Maximum pool size: 10, Minimum idle: 5
     * 
     * 4. Batch Processing:
     *    - Enabled batch inserts/updates for better performance
     *    - Batch size: 20 (configured in application.properties)
     * 
     * 5. Schema:
     *    - Default schema is set to 'public' (PostgreSQL default)
     * 
     * 6. Indexes:
     *    - Strategic indexes added to entities for common query patterns
     *    - Additional indexes created in data.sql for composite queries
     * 
     * 7. Foreign Keys:
     *    - All relationships have named foreign key constraints
     *    - Improves database integrity and query optimization
     * 
     * 8. Data Types:
     *    - TEXT for large content fields
     *    - NUMERIC(19,2) for decimal values
     *    - VARCHAR with appropriate lengths for string fields
     *    - DATE for date fields (LocalDate mapping)
     */

}
