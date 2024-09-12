package com.intela.realestatebackend.util;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Please enter a valid token");
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
                propertyImage -> propertyImageResponses.add(
                        new PropertyImageResponse(propertyImage)
                )
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
        return new PropertyImageResponse(propertyImage);
    }

    public static RetrieveProfileResponse mapToRetrieveProfileResponse(Profile user) {
        // Implement mapping logic here
        return new RetrieveProfileResponse(user);
    }

    public static UpdateProfileResponse mapToUpdateProfileResponse(Map<String, Object> updatedFields) {
        // Implement mapping logic here
        UpdateProfileResponse response = new UpdateProfileResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }

    public static RetrieveAccountResponse mapToRetrieveAccountResponse(User user) {
        // Implement mapping logic here
        return new RetrieveAccountResponse(user);
    }

    public static UpdateAccountResponse mapToUpdateAccountResponse(Map<String, Object> updatedFields) {
        UpdateAccountResponse response = new UpdateAccountResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }

    public static Map<String, Object> updateProfileFromRequest(Profile existingInfo, UpdateProfileRequest request) throws IllegalAccessException {
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

    /**
     * Recursively searches through an entity and its child entities, merging every detached child entity.
     * Uses reflection to find fields that represent child entities, guided by @JsonManagedReference and @JsonBackReference.
     * If neither annotation is present, it assumes the field is a child if it's an @Entity or a collection of @Entity.
     *
     * @param <T>           The type of the root entity.
     * @param entityManager The EntityManager to manage the persistence context.
     * @param entity        The root entity.
     * @return The merged root entity.
     */
    public static <T> T recursiveMerge(EntityManager entityManager, T entity) {
        // Perform BFS using a queue to traverse the entity tree
        Queue<Object> queue = new LinkedList<>();
        queue.add(entity);

        while (!queue.isEmpty()) {
            Object currentEntity = queue.poll();

            // Check if the current entity is detached, and merge if necessary
            if (!entityManager.contains(currentEntity)) {
                currentEntity = entityManager.merge(currentEntity);  // Reattach the entity
            }

            // Find and add child entities to the queue
            List<Object> childEntities = findChildEntities(currentEntity);
            if (childEntities != null) {
                queue.addAll(childEntities);
            }
        }

        return entity; // Return the root entity (now attached)
    }

    /**
     * Uses reflection to find child entities in the given entity.
     * It looks for fields that are either @JsonManagedReference (indicating children),
     * or @Entity-annotated types, or collections of @Entity-annotated types.
     * <p>
     * Fields with @JsonBackReference (indicating parents) are ignored to prevent recursion.
     *
     * @param entity The parent entity.
     * @return List of child entities or an empty list if no children are found.
     */
    public static List<Object> findChildEntities(Object entity) {
        List<Object> childEntities = new LinkedList<>();

        // Get all fields of the entity
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                // Ignore fields annotated with @JsonBackReference (they indicate parent relations)
                if (field.isAnnotationPresent(JsonBackReference.class)) {
                    continue;
                }

                Object fieldValue = field.get(entity);

                if (fieldValue != null) {
                    // If the field is annotated with @JsonManagedReference (child relation)
                    if (field.isAnnotationPresent(JsonManagedReference.class)) {
                        childEntities.add(fieldValue);
                    }

                    // If the field itself is an entity (child entity)
                    else if (isEntity(field.getType())) {
                        childEntities.add(fieldValue);
                    }

                    // If the field is a collection (e.g., List or Set) of entities
                    else if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection<?> collection = (Collection<?>) fieldValue;

                        for (Object item : collection) {
                            if (isEntity(item.getClass())) {
                                childEntities.add(item);
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();  // Handle the exception as necessary
            }
        }

        return childEntities;
    }

    /**
     * Checks if the given class is annotated with @Entity.
     *
     * @param clazz The class to check.
     * @return true if the class is annotated with @Entity, false otherwise.
     */
    public static boolean isEntity(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
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
