package crm.controller;

import com.opencsv.CSVReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/csv")
public class CsvUploadController {

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "Please select a CSV file to upload");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            response.put("error", "Only CSV files are accepted");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<String[]> data = new ArrayList<>();
            String[] line;

            while ((line = reader.readNext()) != null) {
                data.add(line);
            }
            reader.close();

            response.put("fileName", file.getOriginalFilename());
            response.put("rowCount", data.size());
            response.put("data", data);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error processing CSV file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
