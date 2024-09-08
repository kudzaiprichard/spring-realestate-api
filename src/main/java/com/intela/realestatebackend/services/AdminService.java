package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.ID;
import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.repositories.ProfileRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.repositories.application.IDRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private IDRepository idRepository;

    public List<RetrieveProfileResponse> listAllProfiles() {
        // Retrieve profiles where property_id is null
        return profileRepository.findAll().stream()
                .map(user -> Util.mapToRetrieveProfileResponse(user))
                .collect(Collectors.toList());
    }

    public void deleteAccount(Integer userId) {
        // Delete the user account by userId
        userRepository.deleteById(userId);
    }

    public UpdateProfileResponse updateProfile(Integer userId, MultipartFile[] images, UpdateProfileRequest request) throws IllegalAccessException {
        Profile user = profileRepository.findByProfileOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user"));
        Set<ID> ids = new HashSet<>();
        Util.multipartFileToIDList(userId, profileRepository, idRepository, images, ids);

        // Update user details based on UpdateProfileRequest
        user.setIds(ids);
        Map<String, Object> updatedFields = Util.updateProfileFromRequest(user, request);
        // Save the updated user
        profileRepository.save(user);

        // Return the updated profile response
        return Util.mapToUpdateProfileResponse(updatedFields);
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
        return Util.mapToUpdateAccountResponse(updatedFields);
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
