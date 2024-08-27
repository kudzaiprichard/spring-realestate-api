package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.application.CustomerInformation;
import com.intela.realestatebackend.repositories.application.CustomerInformationRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerInformationRepository customerInformationRepository;

    public List<RetrieveProfileResponse> listAllProfiles() {
        // Retrieve profiles where property_id is null
        return customerInformationRepository.findAll().stream()
                .filter(user -> Util.isCustomerProfile(user))
                .map(user -> mapToRetrieveProfileResponse(user))
                .collect(Collectors.toList());
    }

    public void deleteAccount(Integer userId) {
        // Delete the user account by userId
        userRepository.deleteById(userId);
    }

    public UpdateProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) throws IllegalAccessException {
        // Find the CustomerInformation associated with the userId where propertyId is null
        CustomerInformation user = customerInformationRepository.findByUserIdAndPropertyIsNull(userId)
                .orElseThrow(() -> new RuntimeException("CustomerInformation with propertyId == null not found for user"));

        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = updateProfileFromRequest(user, request);

        // Save the updated user
        customerInformationRepository.save(user);

        // Return the updated profile response
        return mapToUpdateProfileResponse(user, updatedFields);
    }

    public UpdateAccountResponse updateAccount(Integer userId, UpdateAccountRequest request) throws IllegalAccessException {
        // Find the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = updateAccountFromRequest(user, request);

        // Save the updated user
        userRepository.save(user);

        // Return the updated profile response
        return mapToUpdateAccountResponse(user, updatedFields);
    }

    public List<RetrieveAccountResponse> listAllAccounts() {
        // Retrieve all accounts and map them to RetrieveAccountResponse objects
        return userRepository.findAll().stream()
                .map(user -> mapToRetrieveAccountResponse(user))
                .collect(Collectors.toList());
    }

    public void banAccount(Integer userId, Timestamp bannedTill) {
        // Retrieve the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Set the bannedTill timestamp
        user.setBannedTill(bannedTill);

        // Save the updated user back to the repository
        userRepository.save(user);
    }
    // Helper methods for mapping and updating

    private RetrieveProfileResponse mapToRetrieveProfileResponse(CustomerInformation user) {
        // Implement mapping logic here
        return (RetrieveProfileResponse) user;
    }

    private UpdateProfileResponse mapToUpdateProfileResponse(CustomerInformation user, Map<String, Object> updatedFields) {
        // Implement mapping logic here
        UpdateProfileResponse response = new UpdateProfileResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }

    private RetrieveAccountResponse mapToRetrieveAccountResponse(User user) {
        // Implement mapping logic here
        return (RetrieveAccountResponse) user;
    }

    private UpdateAccountResponse mapToUpdateAccountResponse(User user, Map<String, Object> updatedFields) {
        UpdateAccountResponse response = new UpdateAccountResponse();
        response.setUpdatedFields(updatedFields);
        return response;
    }
    private Map<String, Object> updateProfileFromRequest(CustomerInformation existingInfo, UpdateProfileRequest request) throws IllegalAccessException {
        Map<String, Object> updatedFields = new HashMap<>();

        // Update ContactDetails if changed
        if (!Objects.equals(existingInfo.getContactDetails(), request.getContactDetails())) {
            existingInfo.setContactDetails(request.getContactDetails());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getContactDetails()), request.getContactDetails());
        }

        // Update EmergencyContacts if changed
        if (!Objects.equals(existingInfo.getEmergencyContacts(), request.getEmergencyContacts())) {
            existingInfo.setEmergencyContacts(request.getEmergencyContacts());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getEmergencyContacts()), request.getEmergencyContacts());
        }

        // Update ResidentialHistories if changed
        if (!Objects.equals(existingInfo.getResidentialHistories(), request.getResidentialHistories())) {
            existingInfo.setResidentialHistories(request.getResidentialHistories());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getResidentialHistories()), request.getResidentialHistories());
        }

        // Update EmploymentHistories if changed
        if (!Objects.equals(existingInfo.getEmploymentHistories(), request.getEmploymentHistories())) {
            existingInfo.setEmploymentHistories(request.getEmploymentHistories());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getEmploymentHistories()), request.getEmploymentHistories());
        }

        // Update PersonalDetails if changed
        if (!Objects.equals(existingInfo.getPersonalDetails(), request.getPersonalDetails())) {
            existingInfo.setPersonalDetails(request.getPersonalDetails());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getPersonalDetails()), request.getPersonalDetails());
        }

        // Update IDs if changed
        if (!Objects.equals(existingInfo.getIds(), request.getIds())) {
            existingInfo.setIds(request.getIds());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getIds()), request.getIds());
        }

        // Update References if changed
        if (!Objects.equals(existingInfo.getReferences(), request.getReferences())) {
            existingInfo.setReferences(request.getReferences());
            updatedFields.put(Util.getQualifiedFieldName(existingInfo, existingInfo.getReferences()), request.getReferences());
        }

        return updatedFields;

    }

    private Map<String, Object> updateAccountFromRequest(User user, UpdateAccountRequest request) throws IllegalAccessException {
        Map<String, Object> updatedFields = new HashMap<>();

        // Update firstName if changed
        if (!Objects.equals(user.getFirstName(), request.getFirstName())) {
            user.setFirstName(request.getFirstName());
            updatedFields.put(Util.getQualifiedFieldName(user, user.getFirstName()), request.getFirstName());
        }

        // Update lastName if changed
        if (!Objects.equals(user.getLastName(), request.getLastName())) {
            user.setLastName(request.getLastName());
            updatedFields.put(Util.getQualifiedFieldName(user, user.getLastName()), request.getLastName());
        }

        // Update mobileNumber if changed
        if (!Objects.equals(user.getMobileNumber(), request.getMobileNumber())) {
            user.setMobileNumber(request.getMobileNumber());
            updatedFields.put(Util.getQualifiedFieldName(user, user.getMobileNumber()), request.getMobileNumber());
        }

        // Update email if changed
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            user.setEmail(request.getEmail());
            updatedFields.put(Util.getQualifiedFieldName(user, user.getEmail()), request.getEmail());
        }

        // Update role if changed
        if (!Objects.equals(user.getRole(), request.getRole())) {
            user.setRole(request.getRole());
            updatedFields.put(Util.getQualifiedFieldName(user, user.getRole()), request.getRole());
        }

        // Add additional fields as needed
        // For example:
        // updateField(user, request, "anotherField", updatedFields);

        return updatedFields;
    }
}
