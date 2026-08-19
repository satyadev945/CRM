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

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Controller
@Slf4j
public class PdfController {

    private PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    private void generateSamplePdf(String fileName, String text) throws DocumentException {
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }
        Document document = new Document();
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Paragraph paragraph = new Paragraph(text);
        document.add(paragraph);
        document.close();

        byte[] pdfBytes = outputStream.toByteArray();
        uploadToAzureBlob(fileName, pdfBytes);
    }

    private void uploadToAzureBlob(String fileName, byte[] data) {
        try {
            String connectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
            String containerName = System.getenv("AZURE_STORAGE_CONTAINER_NAME");
            if (connectionString == null || containerName == null) {
                log.error("Azure Storage configuration missing");
                return;
            }
            com.azure.storage.blob.BlobServiceClient blobServiceClient = new com.azure.storage.blob.BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            com.azure.storage.blob.BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            com.azure.storage.blob.BlobClient blobClient = containerClient.getBlobClient(fileName);
            blobClient.upload(new java.io.ByteArrayInputStream(data), data.length, true);
            log.info("Successfully uploaded {} to Azure Blob Storage", fileName);
        } catch (Exception e) {
            log.error("Failed to upload PDF to Azure Blob Storage: {}", e.getMessage());
        }
    }

    @GetMapping("/pdf-generator")
    public String pdfGenerator(Model model) {
        model.addAttribute("pdf", new Pdf());
        return "pdf/generator";
    }

    @PostMapping("/pdf-generator")
    public String generatePdf(@Valid Pdf pdf, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/pdf-generator";
        } else {
            try {
                generateSamplePdf(pdf.getName(), pdf.getContent());
                pdfService.savePdf(pdf);
            } catch (DocumentException e) {
                log.info("Document");
            }
            return "pdf/success";
        }
    }

}
