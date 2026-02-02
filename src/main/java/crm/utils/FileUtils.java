package crm.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for handling file operations in cloud environments.
 * Replaces desktop GUI-based ReadDataUtils with web-compatible alternatives.
 */
@Slf4j
public class FileUtils {

    /**
     * Validates if a file has an allowed extension
     *
     * @param fileName the name of the file to check
     * @param allowedExtensions list of allowed file extensions
     * @return true if the file has an allowed extension, false otherwise
     */
    public static boolean hasAllowedExtension(String fileName, String... allowedExtensions) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String fileExtension = getFileExtension(fileName);
        List<String> allowedExtensionList = Arrays.asList(allowedExtensions);

        return allowedExtensionList.contains(fileExtension.toLowerCase());
    }

    /**
     * Extract the extension from a filename
     *
     * @param fileName the filename to extract extension from
     * @return the file extension (without the dot) or empty string if none
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty() || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Read a MultipartFile into a byte array
     *
     * @param file the MultipartFile to read
     * @return the file content as byte array
     * @throws IOException if reading the file fails
     */
    public static byte[] readFileToByteArray(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }

        try (InputStream is = file.getInputStream()) {
            return readInputStreamToByteArray(is);
        }
    }

    /**
     * Read an InputStream into a byte array
     *
     * @param is the InputStream to read
     * @return the content as byte array
     * @throws IOException if reading the stream fails
     */
    public static byte[] readInputStreamToByteArray(InputStream is) throws IOException {
        if (is == null) {
            throw new IOException("InputStream is null");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}