package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.ImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import com.intela.realestatebackend.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> fetchAllProperties(
            @RequestParam Optional<Integer> pageNumber,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> amount
    ) {

        Pageable pageRequest = PageRequest.of(
                pageNumber.orElse(0),
                amount.orElse(20),
                Sort.Direction.ASC,
                sortBy.orElse("id")
        );
        return ResponseEntity.ok(this.customerService.fetchAllProperties(pageRequest));
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
    public ResponseEntity<List<PropertyResponse>> fetchAllBookmarksByUserId(
            HttpServletRequest servletRequest,
            @RequestParam Optional<Integer> pageNumber,
            @RequestParam Optional<String> sortBy,
            @RequestParam Optional<Integer> amount

    ){
        Pageable pageRequest = PageRequest.of(
                pageNumber.orElse(0),
                amount.orElse(20),
                Sort.Direction.ASC,
                sortBy.orElse("id")
        );
        return ResponseEntity.ok(this.customerService.fetchAllBookmarksByUserId(servletRequest, pageRequest));
    }

    @GetMapping("/bookmark/{bookmarkId}")
    public ResponseEntity<PropertyResponse> fetchBookmarkById(@PathVariable Integer bookmarkId){
        return ResponseEntity.ok(this.customerService.fetchBookmarkById(bookmarkId));
    }
}
