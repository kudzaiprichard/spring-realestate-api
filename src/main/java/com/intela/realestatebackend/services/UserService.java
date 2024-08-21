package com.intela.realestatebackend.services;

import com.intela.realestatebackend.requestResponse.RetrieveProfileRequest;
import com.intela.realestatebackend.requestResponse.RetrieveProfileResponse;
import com.intela.realestatebackend.requestResponse.UpdateProfileRequest;
import com.intela.realestatebackend.requestResponse.UpdateProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public UpdateProfileResponse updateProfile(HttpServletRequest servletRequest, UpdateProfileRequest request) {
        return null;
    }

    public RetrieveProfileResponse retrieveProfile(HttpServletRequest servletRequest, RetrieveProfileRequest request) {
        return null;
    }
}
