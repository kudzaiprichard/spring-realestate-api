package com.intela.realestatebackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dealer")
@RequiredArgsConstructor
public class DealerController {
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("DEALER::TEST");
    }
}
