package crm.csv;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated This standalone utility class uses GUI components and local filesystem access,
 * which are not compatible with containerized environments.
 * Use REST API file upload endpoints in CSVController instead.
 * For testing, use classpath resources instead of GUI file chooser.
 */
@Deprecated
public class CSVTest {

    public static void main(String[] args) {
        System.err.println("CSVTest utility is deprecated and cannot run in containerized environments.");
        System.err.println("This utility uses GUI file chooser which requires a graphical display.");
        System.err.println("Please use REST API file upload endpoints for CSV import functionality.");
        System.err.println("For testing, load CSV files from classpath resources instead.");

        // Example of loading from classpath for testing purposes:
        // try (InputStream is = CSVTest.class.getClassLoader().getResourceAsStream("test-data.csv")) {
        //     if (is != null) {
        //         processCSV(is);
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    /**
     * Example method for processing CSV from InputStream (compatible with REST uploads and classpath resources)
     */
    private static void processCSV(InputStream inputStream) throws IOException {
        CSVReader reader;
        List<Object[]> data = new ArrayList<>();
        reader = new CSVReader(new InputStreamReader(inputStream));
        String[] line;
        while ((line = reader.readNext()) != null) {
            data.add(line);
            if(line.length > 1 && line[1].equals("QUICK SUB")){
                System.out.println(line[0] + "\t" + line[1] + "\t" + line[2]);
            }
        }
    }

}
