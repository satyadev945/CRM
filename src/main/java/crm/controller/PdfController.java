package crm.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.Pdf;
import crm.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@Slf4j
public class PdfController {

    private final PdfService pdfService;
    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket.name:default-bucket}")
    private String bucketName;
    
    @Value("${aws.s3.pdf.prefix:pdfs/}")
    private String pdfPrefix;

    public PdfController(PdfService pdfService, S3Client s3Client) {
        this.pdfService = pdfService;
        this.s3Client = s3Client;
    }

    /**
     * Generates PDF and stores it in Amazon S3 instead of local file system.
     * This ensures data durability and availability in cloud environments.
     */
    private String generateSamplePdfToS3(String fileName, String text) throws DocumentException, IOException {
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }
        
        // Generate PDF in memory
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Paragraph paragraph = new Paragraph(text);
        document.add(paragraph);
        document.close();
        
        // Upload to S3
        String s3Key = pdfPrefix + fileName;
        byte[] pdfBytes = outputStream.toByteArray();
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("application/pdf")
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(pdfBytes));
        
        log.info("PDF successfully uploaded to S3: s3://{}/{}", bucketName, s3Key);
        return s3Key;
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
                String s3Key = generateSamplePdfToS3(pdf.getName(), pdf.getContent());
                pdf.setS3Key(s3Key); // Store S3 key instead of local path
                pdfService.savePdf(pdf);
                model.addAttribute("s3Key", s3Key);
            } catch (DocumentException e) {
                log.error("Error generating PDF document", e);
                model.addAttribute("error", "Failed to generate PDF");
                return "pdf/generator";
            } catch (IOException e) {
                log.error("Error uploading PDF to S3", e);
                model.addAttribute("error", "Failed to upload PDF to S3");
                return "pdf/generator";
            }
            return "pdf/success";
        }
    }

}
