package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.ImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import com.intela.realestatebackend.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> fetchAllProperties() {

        return ResponseEntity.ok(this.customerService.fetchAllProperties());
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<PropertyResponse> fetchPropertyById(@PathVariable Integer propertyId){
        return ResponseEntity.ok(this.customerService.fetchPropertyById(propertyId));
    }

    @GetMapping("/property/images/{propertyId}")
    public ResponseEntity<List<ImageResponse>> fetchAllImagesByPropertyId(@PathVariable Integer propertyId){
        return ResponseEntity.ok(this.customerService.fetchAllImagesByPropertyId(propertyId));
    }

    @PostMapping("/bookmark/add/{propertyId}")
    public ResponseEntity<String> addBooking(@PathVariable Integer propertyId, HttpServletRequest request){
        return ResponseEntity.ok(this.customerService.addBookmark(propertyId, request));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<List<PropertyResponse>> fetchAllBookmarksByUserId(HttpServletRequest servletRequest){
        return ResponseEntity.ok(this.customerService.fetchAllBookmarksByUserId(servletRequest));
    }

    @GetMapping("/bookmark/{bookmarkId}")
    public ResponseEntity<PropertyResponse> fetchBookmarkById(@PathVariable Integer bookmarkId){
        return ResponseEntity.ok(this.customerService.fetchBookmarkById(bookmarkId));
    }
}
