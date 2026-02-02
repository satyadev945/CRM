package crm.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Service interface for CSV processing operations.
 */
public interface CsvService {

    /**
     * Parse CSV data from an input stream
     *
     * @param inputStream The input stream containing CSV data
     * @return List of string arrays representing CSV rows
     */
    List<String[]> parseCsv(InputStream inputStream);

    /**
     * Parse CSV data from an input stream with column headers
     *
     * @param inputStream The input stream containing CSV data
     * @return List of maps where keys are column names and values are cell values
     */
    List<Map<String, String>> parseCsvWithHeaders(InputStream inputStream);

    /**
     * Filter CSV data based on a column value match
     *
     * @param csvData The CSV data as a list of string arrays
     * @param columnIndex The column index to check
     * @param value The value to match
     * @return Filtered list of rows that match the criteria
     */
    List<String[]> filterByColumnValue(List<String[]> csvData, int columnIndex, String value);

    /**
     * Convert CSV data to a specific entity type
     *
     * @param csvData The CSV data as a list of maps
     * @param entityClass The target entity class
     * @return List of entity objects
     */
    <T> List<T> convertToEntities(List<Map<String, String>> csvData, Class<T> entityClass);

    /**
     * Write entity objects to CSV format
     *
     * @param entities The list of entities to convert
     * @param entityClass The entity class
     * @return CSV data as string
     */
    <T> String writeEntitiesAsCsv(List<T> entities, Class<T> entityClass);
}