package com.intela.realestatebackend.util;

import com.intela.realestatebackend.exceptions.MissingAccessTokenException;
import com.intela.realestatebackend.models.Image;
import com.intela.realestatebackend.models.ProfileImage;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.ID;
import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.models.property.Application;
import com.intela.realestatebackend.models.property.Plan;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.property.PropertyImage;
import com.intela.realestatebackend.repositories.ProfileRepository;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.ImageService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@RequiredArgsConstructor
public class Util {

    public static void toFullImage(Image image){
        if (image.getImage() != null)
            image.setImage(decompressImage(image.getImage()));
        else {
            try {
                image.setImage(Util.readFileToBytes(Paths.get(image.getPath(), image.getName()).toString()));
            } catch (IOException e) {
                throw new RuntimeException("Cannot retrieve profile image" + e);
            }
        }
    }

    public static byte[] readFileToBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public static User getUserByToken(HttpServletRequest request, JwtService jwtService, UserRepository userRepository) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MissingAccessTokenException("Please enter a valid token");
        }

        jwtToken = authHeader.split(" ")[1].trim();
        userEmail = jwtService.extractUsername(jwtToken);

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public static byte[] compressImage(byte[] image) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(image);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(image.length);
        byte[] tmp = new byte[4 * 1024];

        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Can't compress image");
        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] image) {
        Inflater inflater = new Inflater();
        inflater.setInput(image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] tmp = new byte[4 * 1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Can't decompress image");
        }
        return outputStream.toByteArray();
    }

    public static PropertyResponse getPropertyResponse(Property property) {
        return new PropertyResponse(property);
    }

    public static PropertyResponse getPropertyById(Integer propertyId,
                                                   PropertyRepository propertyRepository) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
        List<Plan> plans = propertyRepository.findByParentListing(property);
        property.setPlans(new HashSet<>(plans));
        return getPropertyResponse(property);
    }

    public static List<PropertyImageResponse> getImageByPropertyId(int propertyId, PropertyImageRepository propertyImageRepository) {
        List<PropertyImageResponse> propertyImageResponses = new ArrayList<>();
        List<PropertyImage> propertyImages = propertyImageRepository.findAllByPropertyId(propertyId);

        propertyImages.forEach(
                propertyImage -> {
                    PropertyImageResponse profileImageResponse = new PropertyImageResponse(propertyImage);
                    Util.toFullImage(profileImageResponse);
                    propertyImageResponses.add(profileImageResponse);
                }
        );

        return propertyImageResponses;
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
        PropertyImageResponse propertyImageResponse = new PropertyImageResponse(propertyImage);
        Util.toFullImage(propertyImageResponse);
        return propertyImageResponse;
    }

    public static IDImageResponse convertFromIDImageToImageResponse(ID id) {
        if (id == null || id.getImage() == null) {
            return null;
        }
        IDImageResponse idImageResponse = new IDImageResponse(id);
        Util.toFullImage(idImageResponse);
        return idImageResponse;
    }

    public static RetrieveProfileResponse mapToRetrieveProfileResponse(Profile user) {
        // Implement mapping logic here
        return new RetrieveProfileResponse(user);
    }

    public static RetrieveAccountResponse mapToRetrieveAccountResponse(User user) {
        return new RetrieveAccountResponse(user);
    }

    public static UpdateAccountResponse mapToUpdateAccountResponse(Map<String, Object> updatedFields) {
        UpdateAccountResponse response = new UpdateAccountResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }

    public static void updateProfileFromRequest(Profile existingInfo, UpdateProfileRequest request) throws IllegalAccessException {
        // Update ContactDetails if changed
        if (existingInfo.getContactDetails() != null) {
            request.getContactDetails().setId(existingInfo.getContactDetails().getId());
        }
        existingInfo.setContactDetails(request.getContactDetails());

        // Update EmergencyContacts if changed
        existingInfo.setEmergencyContacts(request.getEmergencyContacts());

        // Update ResidentialHistories if changed
        existingInfo.setResidentialHistories(request.getResidentialHistories());

        // Update EmploymentHistories if changed
        existingInfo.setEmploymentHistories(request.getEmploymentHistories());

        // Update PersonalDetails if changed
        existingInfo.setPersonalDetails(request.getPersonalDetails());

        // Update References if changed
        existingInfo.setReferences(request.getReferences());

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

        // Add additional fields as needed
        // For example:
        // updateField(user, request, "anotherField", updatedFields);

        return updatedFields;
    }

    public static void multipartFileToIDList(Integer userId,
                                             ProfileRepository profileRepository,
                                             MultipartFile[] imagesRequest,
                                             Set<ID> ids,
                                             ImageService imageService) {
        Arrays.asList(imagesRequest).forEach(
                imageRequest -> {
                    try {
                        ID id = ID.builder()
                                .image(compressImage(imageRequest.getBytes()))
                                .name(imageRequest.getOriginalFilename())
                                .type(imageRequest.getContentType())
                                .profile(profileRepository.findByProfileOwnerId(userId)
                                        .orElseThrow(() -> new RuntimeException("Profile not found for user")))
                                .build();
                        imageService.storeImage(id);
                        ids.add(id);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not save image: " + e);
                    }
                }
        );
    }

    public static void multipartFileToPropertyImageList(
            Property property,
            MultipartFile[] imagesRequest,
            List<PropertyImage> propertyImages,
            ImageService imageService) {
        Arrays.asList(imagesRequest).forEach(
                imageRequest -> {
                    try {
                        PropertyImage propertyImage = PropertyImage.builder()
                                .image(compressImage(imageRequest.getBytes()))
                                .name(imageRequest.getOriginalFilename())
                                .type(imageRequest.getContentType())
                                .property(property)
                                .build();
                        imageService.storeImage(propertyImage);
                        propertyImages.add(propertyImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not save image: " + e);
                    }
                }
        );
    }

    public static ApplicationResponse mapApplicationToApplicationResponse(Application application) {
        return new ApplicationResponse(application);
    }

    public static ProfileImage multipartFileToProfileImage(User user, MultipartFile image, ImageService imageService) throws IOException {
        ProfileImage profileImage = ProfileImage.builder()
                .image(compressImage(image.getBytes()))
                .name(image.getOriginalFilename())
                .type(image.getContentType())
                .user(user)
                .build();
        imageService.storeImage(profileImage);
        return profileImage;
    }
}
