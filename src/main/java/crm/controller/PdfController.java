package crm.controller;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import crm.entity.Pdf;
import crm.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Controller
@Slf4j
public class PdfController {

    private PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    private void generateSamplePdf(String fileName, String text) throws FileNotFoundException {
        if (!fileName.endsWith(".pdf")) {
            fileName += ".pdf";
        }
        PdfWriter writer = new PdfWriter(new FileOutputStream(fileName));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        document.add(new Paragraph(text));
        document.close();
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
            } catch (FileNotFoundException e) {
                log.info("File Not Found");
            }
            return "pdf/success";
        }
    }

}
