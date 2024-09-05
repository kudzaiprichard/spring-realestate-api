package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.ProfileImage;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.repositories.ProfileImageRepository;
import com.intela.realestatebackend.repositories.ProfileRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.intela.realestatebackend.util.Util.getUserByToken;
import static com.intela.realestatebackend.util.Util.saveImageToDisk;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileImageRepository profileImageRepository;
    @Autowired
    private JwtService jwtService;

    public UpdateProfileResponse updateProfile(HttpServletRequest servletRequest, MultipartFile[] images, UpdateProfileRequest request) throws IllegalAccessException {
        // Find the CustomerInformation associated with the userId where propertyId is null
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        Profile profile = profileRepository.findByProfileOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("CustomerInformation with propertyId == null not found for user"));
        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = Util.updateProfileFromRequest(profile, request);

        // Save the updated user
        profileRepository.save(profile);

        // Return the updated profile response
        return Util.mapToUpdateProfileResponse(updatedFields);
    }

    public UpdateAccountResponse updateAccount(HttpServletRequest servletRequest, UpdateAccountRequest request) throws IllegalAccessException {
        // Find the user by userId
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = Util.updateAccountFromRequest(user, request);

        // Save the updated user
        userRepository.save(user);

        // Return the updated profile response
        return Util.mapToUpdateAccountResponse(updatedFields);
    }

    public RetrieveProfileResponse retrieveProfile(HttpServletRequest servletRequest) {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        Profile profile = profileRepository.findByProfileOwnerId(user.getId())
                .orElseThrow(() -> new RuntimeException("CustomerInformation with propertyId == null not found for user"));
        return new RetrieveProfileResponse(profile);
    }

    public ProfileImageResponse getProfileImage(HttpServletRequest servletRequest) {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        ProfileImage image = profileImageRepository.findByUserId(user.getId());
        return new ProfileImageResponse(image);
    }

    public void updateProfileImage(HttpServletRequest servletRequest, MultipartFile image) throws IOException {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        ProfileImage profileImage = new ProfileImage();
        profileImage.setUser(user);
        profileImage.setImage(image.getBytes());
        profileImage.setName(image.getOriginalFilename());
        profileImage.setType(image.getContentType());
        saveImageToDisk(profileImage);
        profileImageRepository.save(profileImage);
    }
}
