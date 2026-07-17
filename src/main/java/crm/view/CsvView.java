package crm.view;

import crm.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.io.bean.ICsvBeanWriter;
import org.supercsv.io.bean.CsvBeanWriter;

import java.util.List;
import java.util.Map;

public class CsvView extends AbstractCsvView {

    @Override
    protected void buildCsvDocument(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Content-Disposition", "attachment; filename=\"my-csv-file.csv\"");

        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) model.get("users");
        String[] header = {"FirstName", "LastName", "Username", "Email", "Password", "Enabled", "Role_id", "Role_name"};
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        csvWriter.writeHeader(header);

        for (User user : users) {
            csvWriter.write(user, header);
        }
        csvWriter.close();

    }

}
