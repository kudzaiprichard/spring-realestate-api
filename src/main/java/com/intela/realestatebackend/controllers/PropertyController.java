package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.PropertyImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import com.intela.realestatebackend.services.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping("/")
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
        return ResponseEntity.ok(this.propertyService.fetchAllProperties(pageRequest));
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponse> fetchPropertyById(@PathVariable Integer propertyId) {
        return ResponseEntity.ok(this.propertyService.fetchPropertyById(propertyId));
    }

    @GetMapping("/images/{propertyId}")
    public ResponseEntity<List<PropertyImageResponse>> fetchAllImagesByPropertyId(@PathVariable Integer propertyId) {
        return ResponseEntity.ok(this.propertyService.fetchAllImagesByPropertyId(propertyId));
    }
}
