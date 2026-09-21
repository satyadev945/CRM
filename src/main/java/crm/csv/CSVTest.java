package crm.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import crm.utils.ReadDataUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVTest {

    public static void main(String[] args) {
        File document = ReadDataUtils.readFile("Select CSV file", null, "Only CSV Files", "csv");

        List<Object[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(document))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                data.add(line);
                if (line.length > 2 && "QUICK SUB".equals(line[1])) {
                    System.out.println(line[0] + "\t" + line[1] + "\t" + line[2]);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
