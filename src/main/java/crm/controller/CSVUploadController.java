package crm.controller;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CSVUploadController {

    @GetMapping("/csv/upload")
    public String showUploadForm() {
        return "csv/upload";
    }

    @PostMapping("/csv/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a CSV file to upload");
            return "csv/upload";
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            model.addAttribute("error", "Only CSV files are allowed");
            return "csv/upload";
        }

        try {
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())));
            List<String[]> data = new ArrayList<>();
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
            }
            reader.close();

            model.addAttribute("data", data);
            model.addAttribute("fileName", fileName);
            return "csv/show";
        } catch (IOException e) {
            model.addAttribute("error", "Error processing CSV file: " + e.getMessage());
            return "csv/upload";
        }
    }
}
