package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PropertyImageService {

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Value("${application.custom.file-storage.image-directory}")
    private String imageStorageDirectory; // Define your storage directory

    public PropertyImage storePropertyImage(MultipartFile file, Property property) throws IOException {
        // Step 1: Save the image file to the filesystem or database
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(imageStorageDirectory, fileName);
        Files.write(filePath, file.getBytes());

        // Step 2: Create and save the Image entity
        PropertyImage image = new PropertyImage();
        image.setImage(file.getBytes());
        image.setName(fileName);
        image.setPath(filePath.toString());
        image.setType(file.getContentType());
        image.setProperty(property);

        // Step 4: Save the PropertyImage entity
        return propertyImageRepository.save(image);
    }
}
