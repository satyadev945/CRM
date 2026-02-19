package crm.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import crm.entity.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Cloud-ready PDF View
 * Security fix: Removed password exposure in export functionality
 */
@Slf4j
public class PdfView extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, 
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {
        // change the file name
        response.setHeader("Content-Disposition", "attachment; filename=\"users-export.pdf\"");

        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) model.get("users");
        document.add(new Paragraph("Generated Users Report - " + LocalDate.now()));

        // Calculate column count without password field (security fix)
        int columnCount = users.stream().findAny().map(User::getColumnCount).orElse(7) - 1;
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100.0f);
        table.setSpacingBefore(10);

        // define font for table header row
        Font font = FontFactory.getFont(FontFactory.TIMES);
        font.setColor(BaseColor.WHITE);

        // define table header cell
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setPadding(5);

        // write table header - SECURITY FIX: Removed password column
        cell.setPhrase(new Phrase("First Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Username", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);

        // SECURITY FIX: Password column removed
        // cell.setPhrase(new Phrase("Password", font));
        // table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Role_id", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Role_name", font));
        table.addCell(cell);

        // Write table data - SECURITY FIX: Password not included
        for(User user : users){
            table.addCell(user.getFirstName() != null ? user.getFirstName() : "");
            table.addCell(user.getLastName() != null ? user.getLastName() : "");
            table.addCell(user.getUsername() != null ? user.getUsername() : "");
            table.addCell(user.getEmail() != null ? user.getEmail() : "");
            // SECURITY FIX: Password not exported
            // table.addCell(user.getPassword());
            table.addCell(String.valueOf(user.getEnabled()));
            table.addCell(user.getRole() != null ? String.valueOf(user.getRole().getId()) : "");
            table.addCell(user.getRole() != null ? user.getRole().getName() : "");
        }

        document.add(table);
        
        log.info("PDF export generated successfully with {} users (passwords excluded for security)", 
                users.size());
    }

}
