package crm.controller;

import crm.service.CsvService;
import crm.service.StorageService;
import crm.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for CSV operations.
 * Cloud-compatible replacement for desktop GUI-based CSVTest class.
 */
@Controller
@RequestMapping("/csv")
@Slf4j
public class CsvController {

    private final CsvService csvService;
    private final StorageService storageService;

    public CsvController(CsvService csvService, StorageService storageService) {
        this.csvService = csvService;
        this.storageService = storageService;
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "csv/upload";
    }

    @PostMapping("/upload")
    public String handleCsvUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "filterColumn", required = false, defaultValue = "-1") int filterColumn,
            @RequestParam(value = "filterValue", required = false) String filterValue,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a CSV file to upload");
            return "redirect:/csv/upload";
        }

        if (!FileUtils.getFileExtension(file.getOriginalFilename()).equals("csv")) {
            redirectAttributes.addFlashAttribute("error", "Please upload a valid CSV file");
            return "redirect:/csv/upload";
        }

        try {
            // Save the original CSV file
            byte[] csvBytes = FileUtils.readFileToByteArray(file);
            String fileName = file.getOriginalFilename();
            storageService.storeFile(fileName, csvBytes);

            // Parse the CSV data
            List<String[]> csvData = csvService.parseCsv(new ByteArrayInputStream(csvBytes));

            // Apply filtering if requested
            if (filterColumn >= 0 && filterValue != null && !filterValue.isEmpty()) {
                List<String[]> filteredData = csvService.filterByColumnValue(csvData, filterColumn, filterValue);
                model.addAttribute("csvData", filteredData);
                model.addAttribute("filtered", true);
                model.addAttribute("filterColumn", filterColumn);
                model.addAttribute("filterValue", filterValue);
                model.addAttribute("originalCount", csvData.size());
                model.addAttribute("filteredCount", filteredData.size());
            } else {
                model.addAttribute("csvData", csvData);
                model.addAttribute("filtered", false);
            }

            // Add metadata for display
            model.addAttribute("fileName", fileName);
            model.addAttribute("rowCount", csvData.size());

            if (!csvData.isEmpty()) {
                model.addAttribute("columnCount", csvData.get(0).length);
                if (csvData.size() > 1) {
                    model.addAttribute("headers", csvData.get(0));
                }
            }

            return "csv/view";
        } catch (IOException e) {
            log.error("Failed to process CSV file {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to process CSV file: " + e.getMessage());
            return "redirect:/csv/upload";
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadCsvFile(@PathVariable String fileName) {
        try {
            // Check if file exists
            if (!storageService.fileExists(fileName)) {
                return ResponseEntity.notFound().build();
            }

            // Get file from storage
            byte[] csvBytes = storageService.getFile(fileName);
            ByteArrayResource resource = new ByteArrayResource(csvBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(csvBytes.length)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading CSV file {}: {}", fileName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}