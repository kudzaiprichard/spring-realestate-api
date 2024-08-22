package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.RetrieveProfileResponse;
import com.intela.realestatebackend.requestResponse.UpdateProfileRequest;
import com.intela.realestatebackend.requestResponse.UpdateProfileResponse;
import com.intela.realestatebackend.services.AdminService;
import com.intela.realestatebackend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("ADMIN::TEST");
    }

    @GetMapping("/user-management")
    public ResponseEntity<List<RetrieveProfileResponse>> listAllAccounts(){
        return ResponseEntity.ok(this.adminService.listAllAccounts());
    }

    @DeleteMapping("/user-management/{userId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Integer userId){
        this.adminService.deleteAccount();
        return ResponseEntity.ok("User "+userId+"deleted");
    }

    @PostMapping("/user-management/{userId}")
    public ResponseEntity<UpdateProfileResponse> updateAccount(@PathVariable Integer userId, @RequestBody UpdateProfileRequest request){
        return ResponseEntity.ok(this.adminService.updateAccount());
    }
}
