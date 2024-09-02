package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.CustomerInformation;
import com.intela.realestatebackend.repositories.CustomerInformationRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.intela.realestatebackend.util.Util.getUserByToken;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerInformationRepository customerInformationRepository;
    @Autowired
    private JwtService jwtService;
    public UpdateProfileResponse updateProfile(HttpServletRequest servletRequest, UpdateProfileRequest request) throws IllegalAccessException {
        // Find the CustomerInformation associated with the userId where propertyId is null
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        CustomerInformation customerInformation = customerInformationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CustomerInformation with propertyId == null not found for user"));
        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = Util.updateProfileFromRequest(customerInformation, request);

        // Save the updated user
        customerInformationRepository.save(customerInformation);

        // Return the updated profile response
        return Util.mapToUpdateProfileResponse(customerInformation, updatedFields);
    }

    public UpdateAccountResponse updateAccount(HttpServletRequest servletRequest, UpdateAccountRequest request) throws IllegalAccessException {
        // Find the user by userId
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Update user details based on UpdateProfileRequest
        Map<String, Object> updatedFields = Util.updateAccountFromRequest(user, request);

        // Save the updated user
        userRepository.save(user);

        // Return the updated profile response
        return Util.mapToUpdateAccountResponse(user, updatedFields);
    }

    public RetrieveProfileResponse retrieveProfile(HttpServletRequest servletRequest) {
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        CustomerInformation customerInformation = customerInformationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CustomerInformation with propertyId == null not found for user"));
        return (RetrieveProfileResponse) customerInformation;
    }
}
