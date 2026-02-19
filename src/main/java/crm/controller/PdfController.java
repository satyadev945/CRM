package crm.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.Pdf;
import crm.service.PdfService;
import crm.service.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Controller
@Slf4j
public class PdfController {

    private final PdfService pdfService;
    private final S3StorageService s3StorageService;

    @Autowired
    public PdfController(PdfService pdfService, S3StorageService s3StorageService) {
        this.pdfService = pdfService;
        this.s3StorageService = s3StorageService;
    }

    /**
     * Generate PDF and store in AWS S3 instead of local file system
     * Cloud-ready implementation with distributed tracing support
     */
    private String generateSamplePdf(String fileName, String text) throws DocumentException, IOException {
        // Add correlation ID for distributed tracing
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put("correlationId", correlationId);
        }
        
        log.info("Generating PDF with correlationId: {}, fileName: {}", correlationId, fileName);
        
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }
        
        // Generate PDF in memory instead of writing to local file system
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Paragraph paragraph = new Paragraph(text);
            document.add(paragraph);
            document.close();
            
            // Upload to S3 instead of local file system
            byte[] pdfBytes = outputStream.toByteArray();
            String s3Key = s3StorageService.uploadPdf(fileName, pdfBytes);
            
            log.info("PDF generated and uploaded to S3 successfully. correlationId: {}, s3Key: {}", correlationId, s3Key);
            return s3Key;
            
        } finally {
            outputStream.close();
            MDC.remove("correlationId");
        }
    }

    @GetMapping("/pdf-generator")
    public String pdfGenerator(Model model) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("PDF generator page accessed. correlationId: {}", correlationId);
        
        model.addAttribute("pdf", new Pdf());
        MDC.remove("correlationId");
        return "pdf/generator";
    }

    @PostMapping("/pdf-generator")
    public String generatePdf(@Valid Pdf pdf, BindingResult bindingResult) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        
        if (bindingResult.hasErrors()) {
            log.warn("PDF generation validation failed. correlationId: {}", correlationId);
            MDC.remove("correlationId");
            return "redirect:/pdf-generator";
        } else {
            try {
                String s3Key = generateSamplePdf(pdf.getName(), pdf.getContent());
                pdf.setS3Key(s3Key); // Store S3 key instead of local file path
                pdfService.savePdf(pdf);
                log.info("PDF saved successfully. correlationId: {}, s3Key: {}", correlationId, s3Key);
            } catch (DocumentException e) {
                log.error("Document generation error. correlationId: {}, error: {}", correlationId, e.getMessage(), e);
            } catch (IOException e) {
                log.error("IO error during PDF generation. correlationId: {}, error: {}", correlationId, e.getMessage(), e);
            } finally {
                MDC.remove("correlationId");
            }
            return "pdf/success";
        }
    }

}
