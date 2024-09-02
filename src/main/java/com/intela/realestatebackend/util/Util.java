package com.intela.realestatebackend.util;

import com.intela.realestatebackend.models.profile.CustomerInformation;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RequiredArgsConstructor
public class Util {

    public static User getUserByToken(HttpServletRequest request, JwtService jwtService, UserRepository userRepository) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String jwtToken;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new RuntimeException("Please enter a valid token");
        }

        jwtToken = authHeader.split(" ")[1].trim();
        userEmail = jwtService.extractUsername(jwtToken);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public static byte[] compressImage(byte[] image){
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(image);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(image.length);
        byte[] tmp = new byte[4*1024];

        while(!deflater.finished()){
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }

        try{
            outputStream.close();
        }catch (IOException e){
            throw new RuntimeException("Can't compress image");
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] image){
        Inflater inflater = new Inflater();
        inflater.setInput(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] tmp = new byte[4*1024];

        try{
            while(!inflater.finished()){
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        }catch (Exception e){
            throw new RuntimeException("Can't decompress image");
        }
        return outputStream.toByteArray();
    }

    public static PropertyResponse getPropertyResponse(Property property,
                                                       UserRepository userRepository) {
        return (PropertyResponse) property;
    }

    public static PropertyResponse getPropertyById(Integer propertyId,
                                                   PropertyRepository propertyRepository,
                                                   UserRepository userRepository) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(()-> new EntityNotFoundException("Property not found"));
        List<String> imageResponses = new ArrayList<>();

        property.getPropertyImages().forEach(propertyImage1 -> imageResponses.add(propertyImage1.getName()));

        return getPropertyResponse(property, userRepository);
    }

    public static List<PropertyImageResponse> getImageByPropertyId(int propertyId, PropertyImageRepository propertyImageRepository) {
        List<PropertyImageResponse> propertyImageRespons = new ArrayList<>();
        List<PropertyImage> propertyImages = propertyImageRepository.findAllByPropertyId(propertyId);

        propertyImages.forEach(
                propertyImage -> propertyImageRespons.add(
                        PropertyImageResponse.builder()
                                .id(propertyImage.getId())
                                .type(propertyImage.getType())
                                .name(propertyImage.getName())
                                .image(decompressImage(propertyImage.getImage()))
                                .build()
                )
        );

        return propertyImageRespons;
    }

    public static String getQualifiedFieldName(Object parentObject, Object fieldObject) throws IllegalAccessException {
        Class<?> parentClass = parentObject.getClass();

        for (Field field : parentClass.getDeclaredFields()) {
            field.setAccessible(true);  // In case the field is private

            if (field.get(parentObject) == fieldObject) {
                return parentClass.getName() + "." + field.getName();
            }
        }

        throw new IllegalArgumentException("The given field object is not a field of the parent object.");
    }

    public static PropertyImageResponse convertFromPropertyImageToImageResponse(PropertyImage propertyImage) {
        if (propertyImage == null || propertyImage.getImage() == null) {
            return null;
        }
        PropertyImageResponse propertyImageResponse = new PropertyImageResponse();
        propertyImageResponse.setId(propertyImage.getId());
        propertyImageResponse.setImage(propertyImage.getImage());
        propertyImageResponse.setName(propertyImage.getName());
        propertyImageResponse.setType(propertyImage.getType());
        propertyImageResponse.setPropertyId(propertyImage.getProperty().getId());

        return propertyImageResponse;
    }

    public static RetrieveProfileResponse mapToRetrieveProfileResponse(CustomerInformation user) {
        // Implement mapping logic here
        return (RetrieveProfileResponse) user;
    }

    public static UpdateProfileResponse mapToUpdateProfileResponse(CustomerInformation user, Map<String, Object> updatedFields) {
        // Implement mapping logic here
        UpdateProfileResponse response = new UpdateProfileResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }

    public static RetrieveAccountResponse mapToRetrieveAccountResponse(User user) {
        // Implement mapping logic here
        return (RetrieveAccountResponse) user;
    }

    public static UpdateAccountResponse mapToUpdateAccountResponse(User user, Map<String, Object> updatedFields) {
        UpdateAccountResponse response = new UpdateAccountResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }

    public static Map<String, Object> updateProfileFromRequest(CustomerInformation existingInfo, UpdateProfileRequest request) throws IllegalAccessException {
        Map<String, Object> updatedFields = new HashMap<>();

        // Update ContactDetails if changed
        if (!Objects.equals(existingInfo.getContactDetails(), request.getContactDetails())) {
            existingInfo.setContactDetails(request.getContactDetails());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getContactDetails()), request.getContactDetails());
        }

        // Update EmergencyContacts if changed
        if (!Objects.equals(existingInfo.getEmergencyContacts(), request.getEmergencyContacts())) {
            existingInfo.setEmergencyContacts(request.getEmergencyContacts());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getEmergencyContacts()), request.getEmergencyContacts());
        }

        // Update ResidentialHistories if changed
        if (!Objects.equals(existingInfo.getResidentialHistories(), request.getResidentialHistories())) {
            existingInfo.setResidentialHistories(request.getResidentialHistories());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getResidentialHistories()), request.getResidentialHistories());
        }

        // Update EmploymentHistories if changed
        if (!Objects.equals(existingInfo.getEmploymentHistories(), request.getEmploymentHistories())) {
            existingInfo.setEmploymentHistories(request.getEmploymentHistories());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getEmploymentHistories()), request.getEmploymentHistories());
        }

        // Update PersonalDetails if changed
        if (!Objects.equals(existingInfo.getPersonalDetails(), request.getPersonalDetails())) {
            existingInfo.setPersonalDetails(request.getPersonalDetails());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getPersonalDetails()), request.getPersonalDetails());
        }

        // Update IDs if changed
        if (!Objects.equals(existingInfo.getIds(), request.getIds())) {
            existingInfo.setIds(request.getIds());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getIds()), request.getIds());
        }

        // Update References if changed
        if (!Objects.equals(existingInfo.getReferences(), request.getReferences())) {
            existingInfo.setReferences(request.getReferences());
            updatedFields.put(getQualifiedFieldName(existingInfo, existingInfo.getReferences()), request.getReferences());
        }

        return updatedFields;

    }

    public static Map<String, Object> updateAccountFromRequest(User user, UpdateAccountRequest request) throws IllegalAccessException {
        Map<String, Object> updatedFields = new HashMap<>();

        // Update firstName if changed
        if (!Objects.equals(user.getFirstName(), request.getFirstName())) {
            user.setFirstName(request.getFirstName());
            updatedFields.put(getQualifiedFieldName(user, user.getFirstName()), request.getFirstName());
        }

        // Update lastName if changed
        if (!Objects.equals(user.getLastName(), request.getLastName())) {
            user.setLastName(request.getLastName());
            updatedFields.put(getQualifiedFieldName(user, user.getLastName()), request.getLastName());
        }

        // Update mobileNumber if changed
        if (!Objects.equals(user.getMobileNumber(), request.getMobileNumber())) {
            user.setMobileNumber(request.getMobileNumber());
            updatedFields.put(getQualifiedFieldName(user, user.getMobileNumber()), request.getMobileNumber());
        }

        // Update email if changed
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            user.setEmail(request.getEmail());
            updatedFields.put(getQualifiedFieldName(user, user.getEmail()), request.getEmail());
        }

        // Update role if changed
        if (!Objects.equals(user.getRole(), request.getRole())) {
            user.setRole(request.getRole());
            updatedFields.put(getQualifiedFieldName(user, user.getRole()), request.getRole());
        }

        // Add additional fields as needed
        // For example:
        // updateField(user, request, "anotherField", updatedFields);

        return updatedFields;
    }

    public static void multipartFileToImageList(PropertyImageRepository propertyImageRepository, MultipartFile[] imagesRequest, List<PropertyImage> propertyImages) {
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
}
