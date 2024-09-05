package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("ADMIN::TEST");
    }

    @GetMapping("/user-management/profiles")
    public ResponseEntity<List<RetrieveProfileResponse>> listAllProfiles() {
        return ResponseEntity.ok(this.adminService.listAllProfiles());
    }

    @GetMapping("/user-management")
    public ResponseEntity<List<RetrieveAccountResponse>> listAllAccounts() {
        return ResponseEntity.ok(this.adminService.listAllAccounts());
    }

    @DeleteMapping("/user-management/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Integer userId) {
        this.adminService.deleteAccount(userId);
        return ResponseEntity.ok("User " + userId + "deleted");
    }

    @PostMapping("/user-management/profiles/{userId}")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @PathVariable Integer userId,
            @RequestPart("images") MultipartFile[] images,
            @RequestPart("profileJsonData") UpdateProfileRequest request
    ) {
        try {
            return ResponseEntity.ok(this.adminService.updateProfile(userId, images, request));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/user-management/{userId}")
    public ResponseEntity<UpdateAccountResponse> updateAccount(@PathVariable Integer userId, @RequestBody UpdateAccountRequest request) {
        try {
            return ResponseEntity.ok(this.adminService.updateAccount(userId, request));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/user-management/ban/{userId}")
    public ResponseEntity<String> banAccount(@PathVariable Integer userId, @RequestBody Timestamp bannedTill) {
        this.adminService.banAccount(userId, bannedTill);
        return ResponseEntity.ok("User " + userId + "banned");
    }
}
