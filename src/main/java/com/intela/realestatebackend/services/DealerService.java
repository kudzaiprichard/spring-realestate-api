package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.Feature;
import com.intela.realestatebackend.models.Image;
import com.intela.realestatebackend.models.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.FeatureRepository;
import com.intela.realestatebackend.repositories.ImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static com.intela.realestatebackend.util.Util.*;

@Service
@RequiredArgsConstructor
public class DealerService {
    private final PropertyRepository propertyRepository;
    private final FeatureRepository featureRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoggedUserResponse fetchLoggedInUserByToken(
            HttpServletRequest request
    ){
        User user = getUserByToken(request, jwtService, this.userRepository);
        return LoggedUserResponse.builder()
                .firstname(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }

    private void multipartFileToImageList(MultipartFile[] imagesRequest, List<Image> images) {
        Arrays.asList(imagesRequest).forEach(
                imageRequest -> {
                    try {
                        Image image = Image.builder()
                                .image(compressImage(imageRequest.getBytes()))
                                .name(imageRequest.getOriginalFilename())
                                .type(imageRequest.getContentType())
                                .property(null)
                                .build();
                        images.add(imageRepository.save(image));
                    } catch (IOException e) {
                        throw new RuntimeException("Could not save image: " + e);
                    }
                }
        );
    }
    private static PropertyResponse getPropertyResponse(List<String> imageResponses, Property property) {
        return PropertyResponse.builder()
                .id(property.getId())
                .propertyOwnerName(property.getPropertyOwnerName())
                .description(property.getDescription())
                .location(property.getLocation())
                .numberOfRooms(property.getNumberOfRooms())
                .propertyType(property.getPropertyType())
                .price(property.getPrice())
                .status(property.getStatus())
                .feature(FeatureResponse.builder()
                        .bathrooms(property.getFeature().getBathrooms())
                        .bedrooms(property.getFeature().getBedrooms())
                        .storeys(property.getFeature().getStoreys())
                        .lounges(property.getFeature().getLounges())
                        .build())
                .images(imageResponses)
                .build();
    }


    //Fetch all properties by user id
    public List<PropertyResponse> fetchAllPropertiesByUserId(HttpServletRequest request){
        User user = getUserByToken(request, jwtService, this.userRepository);
        List<PropertyResponse> propertyResponses = new ArrayList<>();
        
        this.propertyRepository.findAllByUserId(user.getId())
                        .forEach(property -> {
                            List<String> imageResponses = new ArrayList<>();
                            property.getImages().forEach(image1 -> imageResponses.add(image1.getName()));
                            propertyResponses.add(getPropertyResponse(imageResponses, property));
                        });
        

        return propertyResponses;
    }

    //Add property
    public String addProperty(PropertyRequest propertyRequest,
                                HttpServletRequest servletRequest,
                                MultipartFile[] imagesRequest
    ) throws IOException {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        // final Image savedImage;
        final Feature savedFeature;
        List<Image> images = new ArrayList<>();
        multipartFileToImageList(imagesRequest, images);

        //Create, save property features
        Feature feature = Feature.builder()
                .bathrooms(propertyRequest.getBathrooms())
                .lounges(propertyRequest.getLounges())
                .storeys(propertyRequest.getStoreys())
                .bedrooms(propertyRequest.getBedrooms())
                .build();
        try{
            savedFeature = this.featureRepository.save(feature);
        }catch (Exception e){
            throw new RuntimeException("Could not save features");
        }

        //Create and save property
        Property property = Property.builder()
                .images(images) // saved images
                .location(propertyRequest.getLocation())
                .description(propertyRequest.getDescription())
                .numberOfRooms(propertyRequest.getNumberOfRooms())
                .propertyType(propertyRequest.getPropertyType())
                .feature(savedFeature) //saved features
                .status(propertyRequest.getStatus())
                .price(propertyRequest.getPrice())
                .propertyOwnerName(propertyRequest.getPropertyOwnerName())
                .user(user)
                .build();

        Property savedProperty = this.propertyRepository.save(property);
        //set images property id to saved property
        try{
            images.forEach(image -> image.setProperty(savedProperty));

            this.imageRepository.saveAll(images);
        }catch (Exception e){
            throw new RuntimeException("Could not save image: " + e);
        }
        
        //update, save saved property
        return "Property was successfully saved";
    }

    //Fetch a property by property id
    public PropertyResponse fetchPropertyById(Integer propertyId){
        List<String> imageResponses = new ArrayList<>();
        
        Property property = this.propertyRepository.findById(propertyId)
                .orElseThrow(()-> new EntityNotFoundException("Property not found"));

        property.getImages().forEach(image1 -> imageResponses.add(image1.getName()));

        return getPropertyResponse(imageResponses, property);
    }

    //Todo: Delete a property by property id
    public String deletePropertyByID(Integer propertyId){
        this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        this.propertyRepository.deleteById(propertyId);
        return "Property has been deleted successfully";
    }

    //Todo: Update a property by property id
    public String updatePropertyById(PropertyRequest property, MultipartFile[] imagesRequest , Integer propertyId){
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        
        //Update Field if not blank
        if(!property.getPropertyType().isBlank()){
            dbProperty.setPropertyType(property.getPropertyType());
        }
        if(!property.getPropertyOwnerName().isBlank()){
            dbProperty.setPropertyOwnerName(property.getPropertyOwnerName());
        }
        if(!property.getLocation().isBlank()){
            dbProperty.setLocation(property.getLocation());
        }
        if(!property.getStatus().isBlank()){
            dbProperty.setStatus(property.getStatus());
        }
        if(!property.getDescription().isBlank()){
            dbProperty.setDescription(property.getDescription());
        }
        if(!property.getPrice().toString().isBlank()){
            dbProperty.setPrice(property.getPrice());
        }
        if(!property.getNumberOfRooms().toString().isBlank()){
            dbProperty.setNumberOfRooms(property.getNumberOfRooms());
        }
        if(!property.getBathrooms().toString().isBlank()){
            dbProperty.getFeature().setBathrooms(property.getBathrooms());
        }
        if(!property.getBedrooms().toString().isBlank()){
            dbProperty.getFeature().setBedrooms(property.getBedrooms());
        }
        if(!property.getLounges().toString().isBlank()){
            dbProperty.getFeature().setLounges(property.getLounges());
        }
        if(!property.getStoreys().toString().isBlank()){
            dbProperty.getFeature().setStoreys(property.getStoreys());
        }

        //Update images
        if(imagesRequest.length > 0){
            List<Image> images = new ArrayList<>();
            multipartFileToImageList(imagesRequest, images);
            List<Image> dbImages = this.imageRepository.findAllByPropertyId(dbProperty.getId());
            Property savedProperty = this.propertyRepository.save(dbProperty);

            //set images property id to saved property
            try{
                this.imageRepository.deleteAll(dbImages);
                images.forEach(image -> image.setProperty(savedProperty));
                this.imageRepository.saveAll(images);
            }catch (Exception e){
                throw new RuntimeException("Could not save image: " + e);
            }
        }
        return "Property updated successfully";
    }
    public String addImagesToProperty(MultipartFile[] imagesRequest, Integer propertyId){
        List<Image> images = new ArrayList<>();
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        multipartFileToImageList(imagesRequest, images);

        //set images property id to saved property
        try{
            images.forEach(image -> image.setProperty(dbProperty));
            this.imageRepository.saveAll(images);
        }catch (Exception e){
            throw new RuntimeException("Could not save image: " + e);
        }

        return "Images added successfully";
    }
    public List<ImageResponse> fetchAllImagesByPropertyId(int propertyId) {
        List<ImageResponse> imageResponses = new ArrayList<>();
        List<Image> images = this.imageRepository.findAllByPropertyId(propertyId);

        images.forEach(
                image -> imageResponses.add(
                            ImageResponse.builder()
                                    .id(image.getId())
                                    .type(image.getType())
                                    .name(image.getName())
                                    .image(decompressImage(image.getImage()))
                                    .build()
                )
        );
        
        return imageResponses;
    }

    public String deleteImageById(Integer imageId){
        this.imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find images"));
        this.imageRepository.deleteById(imageId);
        return "Image deleted successfully";
    }

    //Todo: Fetch list of images
    
    //Todo: Fetch image by property id
}
