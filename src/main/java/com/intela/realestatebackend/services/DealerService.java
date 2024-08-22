package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.property.Feature;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.FeatureRepository;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    private final PropertyImageRepository propertyImageRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    private void multipartFileToImageList(MultipartFile[] imagesRequest, List<PropertyImage> propertyImages) {
        Arrays.asList(imagesRequest).forEach(
                imageRequest -> {
                    try {
                        PropertyImage propertyImage = PropertyImage.builder()
                                .image(compressImage(imageRequest.getBytes()))
                                .name(imageRequest.getOriginalFilename())
                                .type(imageRequest.getContentType())
                                .property(null)
                                .build();
                        propertyImages.add(propertyImageRepository.save(propertyImage));
                    } catch (IOException e) {
                        throw new RuntimeException("Could not save image: " + e);
                    }
                }
        );
    }

    //Fetch all properties by user id
    public List<PropertyResponse> fetchAllPropertiesByUserId(HttpServletRequest request, Pageable pageRequest){
        User user = getUserByToken(request, jwtService, this.userRepository);
        List<PropertyResponse> propertyResponses = new ArrayList<>();
        
        this.propertyRepository.findAllByUserId(user.getId(), pageRequest)
                        .forEach(property -> {
                            List<String> imageResponses = new ArrayList<>();
                            property.getPropertyImages().forEach(propertyImage1 -> imageResponses.add(propertyImage1.getName()));
                            propertyResponses.add(getPropertyResponse(imageResponses, property, this.userRepository));
                        });

        return propertyResponses;
    }

    //Add property
    public void addProperty(PropertyCreationRequest propertyCreationRequest,
                              HttpServletRequest servletRequest,
                              MultipartFile[] imagesRequest
    ) throws IOException {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        // final Image savedImage;
        final Feature savedFeature;
        List<PropertyImage> propertyImages = new ArrayList<>();
        multipartFileToImageList(imagesRequest, propertyImages);

        //Create, save property features
        Feature feature = Feature.builder()
                .bathrooms(propertyCreationRequest.getFeature().getBathrooms())
                .lounges(propertyCreationRequest.getFeature().getLounges())
                .parking(propertyCreationRequest.getFeature().getParking())
                .bedrooms(propertyCreationRequest.getFeature().getBedrooms())
                .build();
        try{
            savedFeature = this.featureRepository.save(feature);
        }catch (Exception e){
            throw new RuntimeException("Could not save features");
        }

        //Create and save property
        Property property = Property.builder()
                .propertyImages(propertyImages) // saved images
                .location(propertyCreationRequest.getLocation())
                .description(propertyCreationRequest.getDescription())
                .numberOfRooms(propertyCreationRequest.getNumberOfRooms())
                .propertyType(propertyCreationRequest.getPropertyType())
                .feature(savedFeature) //saved features
                .status(propertyCreationRequest.getStatus())
                .price(propertyCreationRequest.getPrice())
                .propertyOwnerName(propertyCreationRequest.getPropertyOwnerName())
                .user(user)
                .build();

        Property savedProperty = this.propertyRepository.save(property);
        //set images property id to saved property
        try{
            propertyImages.forEach(propertyImage -> propertyImage.setProperty(savedProperty));

            this.propertyImageRepository.saveAll(propertyImages);
        }catch (Exception e){
            throw new RuntimeException("Could not save image: " + e);
        }
        
        //update, save saved property
        //return "Property was successfully saved";
    }

    //Fetch a property by property id
    public PropertyResponse fetchPropertyById(Integer propertyId){
        return getPropertyById(propertyId, this.propertyRepository, this.userRepository);
    }

    public void deletePropertyByID(Integer propertyId){
        this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        this.propertyRepository.deleteById(propertyId);
        //return "Property has been deleted successfully";
    }


    public void updatePropertyById(PropertyCreationRequest property, MultipartFile[] imagesRequest , Integer propertyId){
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        
        //Update Field if not blank
        if(!(property.getPropertyType() == null)){
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
        if(!property.getFeature().getBathrooms().toString().isBlank()){
            dbProperty.getFeature().setBathrooms(property.getFeature().getBathrooms());
        }
        if(!property.getFeature().getBedrooms().toString().isBlank()){
            dbProperty.getFeature().setBedrooms(property.getFeature().getBedrooms());
        }
        if(!property.getFeature().getLounges().toString().isBlank()){
            dbProperty.getFeature().setLounges(property.getFeature().getLounges());
        }
        if(!property.getFeature().getParking().toString().isBlank()){
            dbProperty.getFeature().setParking(property.getFeature().getParking());
        }

        //Update images
        if(imagesRequest.length > 0){
            List<PropertyImage> propertyImages = new ArrayList<>();
            multipartFileToImageList(imagesRequest, propertyImages);
            List<PropertyImage> dbPropertyImages = this.propertyImageRepository.findAllByPropertyId(dbProperty.getId());
            Property savedProperty = this.propertyRepository.save(dbProperty);

            //set images property id to saved property
            try{
                this.propertyImageRepository.deleteAll(dbPropertyImages);
                propertyImages.forEach(propertyImage -> propertyImage.setProperty(savedProperty));
                this.propertyImageRepository.saveAll(propertyImages);
            }catch (Exception e){
                throw new RuntimeException("Could not save image: " + e);
            }
        }
        //return "Property updated successfully";
    }

    public void addImagesToProperty(MultipartFile[] imagesRequest, Integer propertyId){
        List<PropertyImage> propertyImages = new ArrayList<>();
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        multipartFileToImageList(imagesRequest, propertyImages);

        //set images property id to saved property
        try{
            propertyImages.forEach(propertyImage -> propertyImage.setProperty(dbProperty));
            this.propertyImageRepository.saveAll(propertyImages);
        }catch (Exception e){
            throw new RuntimeException("Could not save image: " + e);
        }

        //return "Images added successfully";
    }
    public List<ImageResponse> fetchAllImagesByPropertyId(int propertyId) {
        return getImageByPropertyId(propertyId, this.propertyImageRepository);
    }

    public void deleteImageById(Integer imageId){
        this.propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find images"));
        this.propertyImageRepository.deleteById(imageId);
        //return "Image deleted successfully";
    }

    public void addPlan(Integer propertyId, PlanCreationRequest planCreationRequest, HttpServletRequest servletRequest, MultipartFile[] images) {
    }

    public List<PlanResponse> listPlansOfProperty(Integer propertyId) {
        return null;
    }

    public void publishProperty(Integer propertyId) {
    }

    public List<ApplicationResponse> listAllApplications() {
        return null;
    }

    public List<ApplicationResponse> listAllApplicationsByPropertyId() {
        return null;
    }

    public void approveApplication(Integer applicationId) {
    }

    public void rejectApplication(Integer applicationId) {
    }

    public void unreadApplication(Integer applicationId) {
    }
}
