package crm.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.Pdf;
import crm.service.PdfService;
import crm.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@Slf4j
public class PdfController {

    private final PdfService pdfService;
    private final StorageService storageService;

    public PdfController(PdfService pdfService, StorageService storageService) {
        this.pdfService = pdfService;
        this.storageService = storageService;
    }

    private String generateSamplePdf(String fileName, String text) {
        try {
            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }

            // Create PDF in memory
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Paragraph paragraph = new Paragraph(text);
            document.add(paragraph);
            document.close();

            // Store PDF in cloud storage
            byte[] pdfBytes = outputStream.toByteArray();
            return storageService.storeFile(fileName, pdfBytes);
        } catch (DocumentException e) {
            log.error("Error generating PDF document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF document", e);
        }
    }

    @GetMapping("/pdf-generator")
    public String pdfGenerator(Model model) {
        model.addAttribute("pdf", new Pdf());
        return "pdf/generator";
    }

    @PostMapping("/pdf-generator")
    public String generatePdf(@Valid Pdf pdf, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "pdf/generator";
        }

        try {
            // Generate and store PDF in cloud storage
            String fileUrl = generateSamplePdf(pdf.getName(), pdf.getContent());

            // Save PDF metadata in database
            pdfService.savePdf(pdf);

            // Add the file URL to model for display in success page
            model.addAttribute("fileUrl", fileUrl);
            return "pdf/success";
        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to generate PDF: " + e.getMessage());
            return "pdf/generator";
        }
    }

    @GetMapping("/pdf/{name}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String name) {
        try {
            // Ensure the name has a pdf extension
            if (!name.endsWith(".pdf")) {
                name += ".pdf";
            }

            // Check if file exists
            if (!storageService.fileExists(name)) {
                return ResponseEntity.notFound().build();
            }

            // Get file from storage
            byte[] pdfBytes = storageService.getFile(name);
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading PDF {}: {}", name, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
