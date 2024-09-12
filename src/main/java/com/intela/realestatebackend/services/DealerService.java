package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.ApplicationStatus;
import com.intela.realestatebackend.models.property.Application;
import com.intela.realestatebackend.models.property.Plan;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.repositories.application.ApplicationRepository;
import com.intela.realestatebackend.requestResponse.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.intela.realestatebackend.util.Util.*;

@Service
@RequiredArgsConstructor
public class DealerService {
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ApplicationRepository applicationRepository;
    private final ImageService imageService;


    //Fetch all properties by user id
    public List<PropertyResponse> fetchAllProperties(HttpServletRequest request, Pageable pageRequest) {
        User user = getUserByToken(request, jwtService, this.userRepository);
        List<PropertyResponse> propertyResponses = new ArrayList<>();

        this.propertyRepository.findAllByUserId(user.getId(), pageRequest)
                .forEach(property -> {
                    propertyResponses.add(getPropertyResponse(property));
                });

        return propertyResponses;
    }

    //Add property
    public void addProperty(PropertyRequest propertyRequest,
                            HttpServletRequest servletRequest,
                            MultipartFile[] imagesRequest
    ) throws IOException {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        // final Image savedImage;
        List<PropertyImage> propertyImages = new ArrayList<>();

        //Create and save property
        propertyRequest.setUser(user);
        //set images property id to saved property

        try {
            multipartFileToPropertyImageList(propertyRequest,
                    imagesRequest,
                    propertyImages,
                    imageService);
            propertyRequest.setPropertyImages(propertyImages);
        } catch (Exception e) {
            throw new RuntimeException("Could not save image: " + e);
        }

        //update, save saved property
        //return "Property was successfully saved";
        this.propertyRepository.save(propertyRequest);
    }

    //Fetch a property by property id
    public PropertyResponse fetchPropertyById(Integer propertyId) {
        return getPropertyById(propertyId, this.propertyRepository);
    }

    public void deletePropertyByID(Integer propertyId) {
        this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        this.propertyRepository.deleteById(propertyId);
        //return "Property has been deleted successfully";
    }


    public void updatePropertyById(PropertyRequest property, MultipartFile[] imagesRequest, Integer propertyId) {
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        //Update Field if not blank
        if (!(property.getPropertyType() == null)) {
            dbProperty.setPropertyType(property.getPropertyType());
        }
        if (!property.getPropertyOwnerName().isBlank()) {
            dbProperty.setPropertyOwnerName(property.getPropertyOwnerName());
        }
        if (!property.getLocation().isBlank()) {
            dbProperty.setLocation(property.getLocation());
        }
        if (!(property.getStatus() == null)) {
            dbProperty.setStatus(property.getStatus());
        }
        if (!property.getDescription().isBlank()) {
            dbProperty.setDescription(property.getDescription());
        }
        if (!property.getPrice().toString().isBlank()) {
            dbProperty.setPrice(property.getPrice());
        }
        if (!property.getNumberOfRooms().toString().isBlank()) {
            dbProperty.setNumberOfRooms(property.getNumberOfRooms());
        }
        if (!property.getFeature().getBathrooms().toString().isBlank()) {
            dbProperty.getFeature().setBathrooms(property.getFeature().getBathrooms());
        }
        if (!property.getFeature().getBedrooms().toString().isBlank()) {
            dbProperty.getFeature().setBedrooms(property.getFeature().getBedrooms());
        }
        if (!property.getFeature().getLounges().toString().isBlank()) {
            dbProperty.getFeature().setLounges(property.getFeature().getLounges());
        }
        if (!property.getFeature().getParking().toString().isBlank()) {
            dbProperty.getFeature().setParking(property.getFeature().getParking());
        }

        //Update images
        if (imagesRequest.length > 0) {
            List<PropertyImage> propertyImages = new ArrayList<>();
            multipartFileToPropertyImageList(dbProperty, imagesRequest, propertyImages, imageService);
            dbProperty.setPropertyImages(propertyImages);
            this.propertyRepository.save(dbProperty);
        }
        //return "Property updated successfully";
    }

    public void addImagesToProperty(MultipartFile[] imagesRequest, Integer propertyId) {
        List<PropertyImage> propertyImages = new ArrayList<>();
        Property dbProperty = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        multipartFileToPropertyImageList(dbProperty, imagesRequest, propertyImages, imageService);

        //set images property id to saved property
        try {
            propertyImages.forEach(propertyImage -> propertyImage.setProperty(dbProperty));
            this.propertyImageRepository.saveAll(propertyImages);
        } catch (Exception e) {
            throw new RuntimeException("Could not save image: " + e);
        }

        //return "Images added successfully";
    }

    public List<PropertyImageResponse> fetchAllImagesByPropertyId(int propertyId) {
        return getImageByPropertyId(propertyId, this.propertyImageRepository);
    }

    public void deleteImageById(Integer imageId) {
        this.propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find images"));
        this.propertyImageRepository.deleteById(imageId);
        //return "Image deleted successfully";
    }

    public void addPlan(Integer propertyId, PlanRequest planRequest, HttpServletRequest servletRequest, MultipartFile[] images) throws IOException {
        // Step 1: Retrieve the property based on the propertyId
        // final Image savedImage;
        List<PropertyImage> propertyImages = new ArrayList<>();
        Property parentListing = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));
        planRequest.setParentListing(parentListing);
        planRequest.setUser(parentListing.getUser());
        try {
            multipartFileToPropertyImageList(planRequest,
                    images,
                    propertyImages, imageService);
        } catch (Exception e) {
            throw new RuntimeException("Could not save image: " + e);
        }
        planRequest.setPropertyImages(propertyImages);
        // Step 5: Persist the Plan object
        propertyRepository.save(planRequest);
    }

    public List<PropertyResponse> listPlansOfProperty(Integer propertyId) {
        // Step 1: Retrieve the Property by its ID
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with id: " + propertyId));

        // Step 2: Retrieve the list of Plans associated with the Property
        List<Plan> plans = propertyRepository.findByParentListing(property);

        // Step 3: Convert the list of Plan entities to PropertyResponse DTOs
        return plans.stream()
                .map(PropertyResponse::new)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> listAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());
    }

    private ApplicationResponse mapToApplicationResponse(Application application) {
        if (application == null) {
            return null;
        }
        return new ApplicationResponse(application);
    }

    public List<ApplicationResponse> listAllApplicationsByPropertyId(Integer propertyId) {
        return applicationRepository.findByPropertyId(propertyId).stream()
                .map(this::mapToApplicationResponse)
                .collect(Collectors.toList());
    }

    public void approveApplication(Integer applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with id: " + applicationId));

        application.setStatus(ApplicationStatus.APPROVED); // Assuming you have a `status` field in `Application`
        applicationRepository.save(application);
    }

    public void rejectApplication(Integer applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with id: " + applicationId));

        application.setStatus(ApplicationStatus.REJECTED); // Assuming you have a `status` field in `Application`
        applicationRepository.save(application);
    }

    public void unreadApplication(Integer applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with id: " + applicationId));

        application.setStatus(ApplicationStatus.UNREAD); // Assuming you have a `status` field in `Application`
        applicationRepository.save(application);
    }

    public ApplicationResponse viewApplication(Integer applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with id: " + applicationId));

        application.setStatus(ApplicationStatus.READ); // Assuming you have a `status` field in `Application`
        applicationRepository.save(application);
        return new ApplicationResponse(application);
    }
}
