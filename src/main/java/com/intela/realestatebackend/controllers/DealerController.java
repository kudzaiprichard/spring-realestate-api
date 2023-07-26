package com.intela.realestatebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intela.realestatebackend.requestResponse.ImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyRequest;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import com.intela.realestatebackend.services.DealerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dealer")
@RequiredArgsConstructor
public class DealerController {
    private final DealerService dealerService;
    private final ObjectMapper objectMapper;
    @PostMapping("/property/add")
    public ResponseEntity<String> addProperty(
            @RequestParam("images") MultipartFile[] images,
            @RequestParam(value="propertyJsonData")String propertyJsonData,
            HttpServletRequest servletRequest
    ){
        PropertyRequest propertyRequest;
        try{
            propertyRequest = objectMapper.readValue(propertyJsonData, PropertyRequest.class);

        }catch (Exception e){
            throw new RuntimeException("Failed to phase json to object");
        }

        try {
            return ResponseEntity.created(URI.create("")).body(
                    dealerService.addProperty(
                            propertyRequest,
                            servletRequest,
                            images
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponse>> fetchAllPropertiesByUserId(
            HttpServletRequest request,
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
        return ResponseEntity.ok(this.dealerService.fetchAllPropertiesByUserId(request,pageRequest));
    }

    //Todo: Endpoint should return images as a list
    @GetMapping("/property/images/{propertyId}")
    public ResponseEntity<byte[]> fetchAllImagesByPropertyId(@PathVariable int propertyId){
        List<ImageResponse> images = this.dealerService.fetchAllImagesByPropertyId(propertyId);
        List<byte[]> imagesByte = new ArrayList<>();

        images.forEach(image -> imagesByte.add(
                image.getImage()
        ));
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/jpg"))
                .body(imagesByte.get(0));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<PropertyResponse> fetchPropertyById(@PathVariable Integer propertyId){
        return ResponseEntity.ok(this.dealerService.fetchPropertyById(propertyId));
    }

    @DeleteMapping("/property/{propertyId}")
    public ResponseEntity<String> deletePropertyByID(@PathVariable Integer propertyId){
        return ResponseEntity.ok(this.dealerService.deletePropertyByID(propertyId));
    }

    @PutMapping("/property/{propertyId}")
    public ResponseEntity<String> updatePropertyById(
            @PathVariable Integer propertyId,
            @RequestParam("images") MultipartFile[] images,
            @RequestParam(value="propertyJsonData")String propertyJsonData
    ){
        PropertyRequest propertyRequest;
        try{
            propertyRequest = objectMapper.readValue(propertyJsonData, PropertyRequest.class);

        }catch (Exception e){
            throw new RuntimeException("Failed to phase json to object");
        }
        return ResponseEntity.ok(this.dealerService.updatePropertyById(propertyRequest ,images, propertyId));
    }

    @PostMapping("/property/{propertyId}")
    public ResponseEntity<String> addImageToProperty(@RequestParam MultipartFile[] images,@PathVariable Integer propertyId){
        return ResponseEntity.ok(this.dealerService.addImagesToProperty(images, propertyId));
    }

    @DeleteMapping("property/image/{imageId}")
    public ResponseEntity<String> deleteImageById(@PathVariable Integer imageId){
        return ResponseEntity.accepted().body(this.dealerService.deleteImageById(imageId));
    }
}
