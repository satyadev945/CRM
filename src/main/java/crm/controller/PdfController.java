package crm.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.Pdf;
import crm.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Cloud-ready PDF Controller that stores generated PDFs in Amazon S3
 * instead of local file system for durability and scalability.
 */
@Controller
@Slf4j
public class PdfController {

    private static final String S3_BUCKET_NAME = System.getenv().getOrDefault("S3_BUCKET_NAME", "crm-pdf-bucket");
    private static final String S3_PDF_PREFIX = System.getenv().getOrDefault("S3_PDF_PREFIX", "pdfs/");

    private PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    /**
     * Generates a PDF and stores it in Amazon S3 instead of local file system.
     * This ensures data durability and availability in cloud environments.
     * 
     * @param fileName Name of the PDF file
     * @param text Content to be written in the PDF
     * @return S3 object key where the PDF was stored, or null if error occurs
     */
    private String generateSamplePdfToS3(String fileName, String text) {
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }

        try {
            // Generate PDF in memory instead of writing to local file system
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            Paragraph paragraph = new Paragraph(text);
            document.add(paragraph);
            document.close();

            // Upload to S3
            byte[] pdfBytes = baos.toByteArray();
            String s3Key = S3_PDF_PREFIX + fileName;

            try (S3Client s3Client = S3Client.builder().build()) {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(S3_BUCKET_NAME)
                        .key(s3Key)
                        .contentType("application/pdf")
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(pdfBytes));
                log.info("Successfully uploaded PDF to S3: s3://{}/{}", S3_BUCKET_NAME, s3Key);
                return s3Key;

            } catch (S3Exception e) {
                log.error("Failed to upload PDF to S3: {}", e.awsErrorDetails().errorMessage());
                return null;
            }

        } catch (DocumentException e) {
            log.error("Error generating PDF document: {}", e.getMessage());
            return null;
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
            String s3Key = generateSamplePdfToS3(pdf.getName(), pdf.getContent());
            if (s3Key != null) {
                // Store S3 location in the entity instead of local file path
                pdf.setS3Key(s3Key);
                pdfService.savePdf(pdf);
                model.addAttribute("s3Location", "s3://" + S3_BUCKET_NAME + "/" + s3Key);
                log.info("PDF generated and stored in S3: {}", s3Key);
                return "pdf/success";
            } else {
                log.error("Failed to generate and store PDF in S3");
                model.addAttribute("error", "Failed to generate PDF");
                return "pdf/generator";
            }
        }
    }

}
