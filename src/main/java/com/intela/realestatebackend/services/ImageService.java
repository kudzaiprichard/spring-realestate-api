package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ImageService {

    @Value("${application.custom.file-storage.image-directory}")
    private String imageStorageDirectory; // Define your storage directory

    public void storeImage(Image image) throws IOException {
        String outputPath = imageStorageDirectory;
        byte[] imageBytes = image.getImage();
        FileOutputStream fileOutputStream = null;
        try {
            // Create a new File object for the output path
            File outputFile = new File(outputPath);

            // Create parent directories if they don't exist
            File parentDirectory = outputFile.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                if (parentDirectory.mkdirs()) {
                    System.out.println("Created directories for the output path: " + parentDirectory.getAbsolutePath());
                } else {
                    throw new IOException("Failed to create directories for the output path: " + parentDirectory.getAbsolutePath());
                }
            }

            // Create a new file output stream to write the bytes to the specified file path
            fileOutputStream = new FileOutputStream(outputFile);

            // Write the byte array to the file
            fileOutputStream.write(imageBytes);
            fileOutputStream.flush();

            System.out.println("Image saved successfully to: " + outputPath);
        } finally {
            // Ensure the stream is closed after use
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
}
