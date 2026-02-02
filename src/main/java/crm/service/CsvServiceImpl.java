package crm.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CsvServiceImpl implements CsvService {

    @Override
    public List<String[]> parseCsv(InputStream inputStream) {
        List<String[]> csvData = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).build()) {
            csvData = reader.readAll();
            log.info("Successfully parsed CSV with {} rows", csvData.size());
        } catch (Exception e) {
            log.error("Error parsing CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse CSV data: " + e.getMessage(), e);
        }

        return csvData;
    }

    @Override
    public List<Map<String, String>> parseCsvWithHeaders(InputStream inputStream) {
        List<Map<String, String>> result = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream)).build()) {
            List<String[]> allData = reader.readAll();

            if (allData.isEmpty()) {
                return result;
            }

            // First row contains headers
            String[] headers = allData.get(0);

            // Process remaining rows
            for (int i = 1; i < allData.size(); i++) {
                String[] row = allData.get(i);
                Map<String, String> rowMap = new HashMap<>();

                // Map each cell to its corresponding header
                for (int j = 0; j < headers.length && j < row.length; j++) {
                    rowMap.put(headers[j], row[j]);
                }

                result.add(rowMap);
            }

            log.info("Successfully parsed CSV with headers ({} rows)", result.size());
        } catch (Exception e) {
            log.error("Error parsing CSV with headers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse CSV data with headers: " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<String[]> filterByColumnValue(List<String[]> csvData, int columnIndex, String value) {
        if (csvData == null || csvData.isEmpty()) {
            return new ArrayList<>();
        }

        return csvData.stream()
                .filter(row -> row.length > columnIndex && row[columnIndex] != null && row[columnIndex].equals(value))
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> convertToEntities(List<Map<String, String>> csvData, Class<T> entityClass) {
        List<T> entities = new ArrayList<>();

        try {
            // Convert List<Map<String, String>> to CSV format
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            // Write headers
            if (!csvData.isEmpty()) {
                csvWriter.writeNext(csvData.get(0).keySet().toArray(new String[0]));

                // Write data
                for (Map<String, String> row : csvData) {
                    csvWriter.writeNext(row.values().toArray(new String[0]));
                }
            }

            csvWriter.close();
            String csvContent = writer.toString();

            // Use OpenCSV to convert CSV to beans
            HeaderColumnNameMappingStrategy<T> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
            mappingStrategy.setType(entityClass);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(new java.io.StringReader(csvContent))
                    .withType(entityClass)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            entities = csvToBean.parse();
            log.info("Successfully converted {} CSV rows to {} entities", csvData.size(), entities.size());
        } catch (Exception e) {
            log.error("Error converting CSV to entities: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert CSV data to entities: " + e.getMessage(), e);
        }

        return entities;
    }

    @Override
    public <T> String writeEntitiesAsCsv(List<T> entities, Class<T> entityClass) {
        if (entities == null || entities.isEmpty()) {
            return "";
        }

        try {
            StringWriter writer = new StringWriter();

            HeaderColumnNameMappingStrategy<T> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
            mappingStrategy.setType(entityClass);

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            beanToCsv.write(entities);
            log.info("Successfully converted {} entities to CSV", entities.size());
            return writer.toString();
        } catch (Exception e) {
            log.error("Error converting entities to CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert entities to CSV: " + e.getMessage(), e);
        }
    }
}