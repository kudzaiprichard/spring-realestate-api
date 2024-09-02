package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.CustomerInformation;
import com.intela.realestatebackend.repositories.CustomerInformationRepository;
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
                .map(user -> Util.mapToRetrieveProfileResponse(user))
                .collect(Collectors.toList());
    }

    public void deleteAccount(Integer userId) {
        // Delete the user account by userId
        userRepository.deleteById(userId);
    }

    public UpdateProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) throws IllegalAccessException {
        // Find the CustomerInformation associated with the userId where propertyId is null
        CustomerInformation user = customerInformationRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("CustomerInformation with propertyId == null not found for user"));

        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = Util.updateProfileFromRequest(user, request);

        // Save the updated user
        customerInformationRepository.save(user);

        // Return the updated profile response
        return Util.mapToUpdateProfileResponse(user, updatedFields);
    }

    public UpdateAccountResponse updateAccount(Integer userId, UpdateAccountRequest request) throws IllegalAccessException {
        // Find the user by userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = Util.updateAccountFromRequest(user, request);

        // Save the updated user
        userRepository.save(user);

        // Return the updated profile response
        return Util.mapToUpdateAccountResponse(user, updatedFields);
    }

    public List<RetrieveAccountResponse> listAllAccounts() {
        // Retrieve all accounts and map them to RetrieveAccountResponse objects
        return userRepository.findAll().stream()
                .map(user -> Util.mapToRetrieveAccountResponse(user))
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

}
