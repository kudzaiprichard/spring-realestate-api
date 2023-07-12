package com.intela.realestatebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
//    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("CUSTOMER::TEST");// + auth.getName()
    }
}
