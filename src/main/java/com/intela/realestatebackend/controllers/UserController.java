package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.RetrieveProfileRequest;
import com.intela.realestatebackend.requestResponse.RetrieveProfileResponse;
import com.intela.realestatebackend.requestResponse.UpdateProfileRequest;
import com.intela.realestatebackend.requestResponse.UpdateProfileResponse;
import com.intela.realestatebackend.services.AuthService;
import com.intela.realestatebackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            HttpServletRequest servletRequest,
            @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok().body(userService.updateProfile(servletRequest, request));
    }

    @GetMapping("/")
    public ResponseEntity<RetrieveProfileResponse> retrieveProfile(
            HttpServletRequest servletRequest,
            @RequestBody RetrieveProfileRequest request
    ) {
        return ResponseEntity.ok().body(userService.retrieveProfile(servletRequest, request));
    }
}
