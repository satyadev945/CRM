package crm.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.Pdf;
import crm.service.PdfService;
import crm.service.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;

/**
 * Cloud-ready PDF Controller
 * Uses S3 storage instead of local file system for cloud deployment
 */
@Controller
@Slf4j
public class PdfController {

    private final PdfService pdfService;
    private final S3StorageService s3StorageService;

    @Value("${app.pdf.storage.type:s3}")
    private String storageType;

    @Autowired
    public PdfController(PdfService pdfService, S3StorageService s3StorageService) {
        this.pdfService = pdfService;
        this.s3StorageService = s3StorageService;
    }

    /**
     * Generate PDF in memory and upload to S3 (cloud-native approach)
     * Replaces local file system write operations
     */
    private String generateSamplePdfToCloud(String fileName, String text) throws DocumentException {
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }
        
        // Generate PDF in memory using ByteArrayOutputStream (cloud-friendly)
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            Paragraph paragraph = new Paragraph(text);
            document.add(paragraph);
            document.close();
            
            // Upload to S3 instead of writing to local file system
            byte[] pdfBytes = baos.toByteArray();
            String s3Key = s3StorageService.uploadPdf(fileName, pdfBytes);
            
            log.info("PDF generated and uploaded to S3: {}", s3Key);
            return s3Key;
        } catch (Exception e) {
            log.error("Failed to generate PDF to cloud storage", e);
            throw new DocumentException("Failed to generate PDF", e);
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
            return "redirect:/pdf-generator";
        } else {
            try {
                // Generate PDF and upload to cloud storage
                String s3Key = generateSamplePdfToCloud(pdf.getName(), pdf.getContent());
                
                // Store S3 key reference in database
                pdf.setName(s3Key);
                pdfService.savePdf(pdf);
                
                // Get public URL for the PDF
                String pdfUrl = s3StorageService.getObjectUrl(s3Key);
                model.addAttribute("pdfUrl", pdfUrl);
                
                log.info("PDF successfully generated and stored: {}", s3Key);
            } catch (DocumentException e) {
                log.error("Failed to generate PDF document", e);
                model.addAttribute("error", "Failed to generate PDF");
                return "pdf/generator";
            } catch (Exception e) {
                log.error("Unexpected error during PDF generation", e);
                model.addAttribute("error", "An unexpected error occurred");
                return "pdf/generator";
            }
            return "pdf/success";
        }
    }

}
