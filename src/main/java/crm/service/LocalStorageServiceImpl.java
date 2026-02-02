package crm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Local filesystem implementation of the StorageService interface.
 * Only for development and testing environments.
 */
@Service
@Profile({"default", "dev", "test"})
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    private final Path storageLocation;

    public LocalStorageServiceImpl(@Value("${storage.location:storage}") String storageLocation) {
        this.storageLocation = Paths.get(storageLocation);
        init();
    }

    private void init() {
        try {
            Files.createDirectories(storageLocation);
            log.info("Storage location initialized: {}", storageLocation.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not initialize storage location: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String storeFile(String fileName, byte[] content) {
        try {
            Path targetPath = storageLocation.resolve(fileName);
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(content);
            }
            log.info("File '{}' stored locally at: {}", fileName, targetPath.toAbsolutePath());
            return targetPath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }

    @Override
    public String storeFile(String fileName, InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] byteArray = buffer.toByteArray();

            return storeFile(fileName, byteArray);
        } catch (IOException e) {
            log.error("Failed to store file from input stream: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file from input stream " + fileName, e);
        }
    }

    @Override
    public byte[] getFile(String fileName) {
        try {
            Path filePath = storageLocation.resolve(fileName);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + fileName);
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read file " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = storageLocation.resolve(fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File '{}' deleted from local storage", fileName);
            } else {
                log.warn("File '{}' not found in local storage", fileName);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file " + fileName, e);
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        Path filePath = storageLocation.resolve(fileName);
        return Files.exists(filePath);
    }
}