package crm.controller;

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

import java.io.IOException;
import java.util.Arrays;

/**
 * Controller for handling file uploads in a cloud-compatible way.
 * Replaces the desktop GUI-based file selection with web-based file uploads.
 */
@Controller
@RequestMapping("/files")
@Slf4j
public class FileUploadController {

    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "files/upload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "allowedExtensions", required = false, defaultValue = "") String allowedExtensions,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/files/upload";
        }

        String fileName = file.getOriginalFilename();

        // Validate file extension if allowedExtensions is provided
        if (allowedExtensions != null && !allowedExtensions.isEmpty()) {
            String[] extensions = allowedExtensions.split(",");
            if (!FileUtils.hasAllowedExtension(fileName, extensions)) {
                redirectAttributes.addFlashAttribute("error",
                    "File type not allowed. Allowed extensions: " + Arrays.toString(extensions));
                return "redirect:/files/upload";
            }
        }

        try {
            byte[] fileBytes = FileUtils.readFileToByteArray(file);
            String fileUrl = storageService.storeFile(fileName, fileBytes);

            redirectAttributes.addFlashAttribute("message",
                "File uploaded successfully: " + fileName);
            redirectAttributes.addFlashAttribute("fileUrl", fileUrl);

            return "redirect:/files/upload";
        } catch (IOException e) {
            log.error("Failed to upload file {}: {}", fileName, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/files/upload";
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Check if file exists
            if (!storageService.fileExists(fileName)) {
                return ResponseEntity.notFound().build();
            }

            // Get file from storage
            byte[] fileBytes = storageService.getFile(fileName);
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            // Determine content type
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (fileName.endsWith(".pdf")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
            } else if (fileName.endsWith(".csv")) {
                contentType = "text/csv";
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                contentType = "application/vnd.ms-excel";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentLength(fileBytes.length)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading file {}: {}", fileName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}