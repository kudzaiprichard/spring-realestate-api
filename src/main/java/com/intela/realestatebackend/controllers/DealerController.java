package com.intela.realestatebackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intela.realestatebackend.requestResponse.*;
import com.intela.realestatebackend.services.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
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

    @Operation(
            summary = "Upload a new property with images",
            description = "Uploads a property JSON and associated image files",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "images", contentType = "image/png, image/jpeg")
                            },
                            mediaType = "multipart/form-data",
                            schemaProperties =
                                    {
                                            @SchemaProperty(
                                                    name = "request",
                                                    schema = @Schema(implementation = PropertyRequest.class)
                                            ),
                                            @SchemaProperty(
                                                    name = "images",
                                                    array = @ArraySchema(
                                                            schema = @Schema(type = "string", format = "binary")
                                                    )
                                            )
                                    }

                    )
            )
    )
    @PostMapping("/property/add")
    public ResponseEntity<String> addProperty(
            @RequestPart("images") MultipartFile[] images,
            @RequestPart(value = "request") PropertyRequest request,
            HttpServletRequest servletRequest
    ) {
        try {
            dealerService.addProperty(
                    request,
                    servletRequest,
                    images
            );
            return ResponseEntity.created(URI.create("")).body(
                    "Property was successfully saved"
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
    ) {
        Pageable pageRequest = PageRequest.of(
                pageNumber.orElse(0),
                amount.orElse(20),
                Sort.Direction.ASC,
                sortBy.orElse("id")
        );
        return ResponseEntity.ok(this.dealerService.fetchAllProperties(request, pageRequest));
    }

    //Todo: Endpoint should return images as a list
    @GetMapping("/property/images/{propertyId}")
    public ResponseEntity<List<PropertyImageResponse>> fetchAllImagesByPropertyId(@PathVariable int propertyId) {
        List<PropertyImageResponse> images = this.dealerService.fetchAllImagesByPropertyId(propertyId);
        List<byte[]> imagesByte = new ArrayList<>();

        images.forEach(image -> imagesByte.add(
                image.getImage()
        ));
        return ResponseEntity.ok(images);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<PropertyResponse> fetchPropertyById(@PathVariable Integer propertyId) {
        return ResponseEntity.ok(this.dealerService.fetchPropertyById(propertyId));
    }

    @DeleteMapping("/property/{propertyId}")
    public ResponseEntity<String> deletePropertyByID(@PathVariable Integer propertyId) {
        this.dealerService.deletePropertyByID(propertyId);
        return ResponseEntity.ok("Property has been deleted successfully");
    }

    @Operation(
            summary = "Updates property",
            description = "Uploads a property JSON object",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "images", contentType = "image/png, image/jpeg")
                            },
                            mediaType = "multipart/form-data",
                            schemaProperties =
                                    {
                                            @SchemaProperty(
                                                    name = "request",
                                                    schema = @Schema(implementation = UpdateProfileRequest.class)
                                            ),
                                            @SchemaProperty(
                                                    name = "images",
                                                    array = @ArraySchema(
                                                            schema = @Schema(type = "string", format = "binary")
                                                    )
                                            )
                                    }

                    )
            )
    )
    @PutMapping("/property/{propertyId}")
    public ResponseEntity<String> updatePropertyById(
            @PathVariable Integer propertyId,
            @RequestParam("images") MultipartFile[] images,
            @RequestParam(value = "request") PropertyRequest request
    ) {
        this.dealerService.updatePropertyById(request, images, propertyId);
        return ResponseEntity.ok("Property updated successfully");
    }

    @PostMapping("/property/{propertyId}")
    public ResponseEntity<String> addImageToProperty(@RequestBody MultipartFile[] images, @PathVariable Integer propertyId) {
        this.dealerService.addImagesToProperty(images, propertyId);
        return ResponseEntity.ok("Images added successfully");
    }

    @DeleteMapping("property/image/{imageId}")
    public ResponseEntity<String> deleteImageById(@PathVariable Integer imageId) {
        this.dealerService.deleteImageById(imageId);
        return ResponseEntity.accepted().body("Image deleted successfully");
    }

    @Operation(
            summary = "Uploads a new plan with images to property",
            description = "Uploads a plan JSON and associated image files",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            encoding = {
                                    @Encoding(name = "request", contentType = "application/json"),
                                    @Encoding(name = "images", contentType = "image/png, image/jpeg")
                            },
                            mediaType = "multipart/form-data",
                            schemaProperties =
                                    {
                                            @SchemaProperty(
                                                    name = "request",
                                                    schema = @Schema(implementation = PlanRequest.class)
                                            ),
                                            @SchemaProperty(
                                                    name = "images",
                                                    array = @ArraySchema(
                                                            schema = @Schema(type = "string", format = "binary")
                                                    )
                                            )
                                    }

                    )
            )
    )
    @PostMapping("/property/plan/add/{propertyId}")
    public ResponseEntity<String> addPlanToProperty(@PathVariable Integer propertyId,
                                                    @RequestPart("images") MultipartFile[] images,
                                                    @RequestPart(value = "request") PlanRequest request,
                                                    HttpServletRequest servletRequest) {
        try {
            dealerService.addPlan(
                    propertyId,
                    request,
                    servletRequest,
                    images
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.created(URI.create("")).body(
                "New lease plan added to property"
        );
    }

    @GetMapping("/property/plans/{propertyId}")
    public ResponseEntity<List<PropertyResponse>> listPlansOfProperty(@PathVariable Integer propertyId) {
        return ResponseEntity.created(URI.create("")).body(
                dealerService.listPlansOfProperty(
                        propertyId
                )
        );
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> listAllApplications() {
        return ResponseEntity.created(URI.create("")).body(
                dealerService.listAllApplications()
        );
    }

    @GetMapping("/applications/{propertyId}")
    public ResponseEntity<List<ApplicationResponse>> listAllApplicationsByPropertyId(@PathVariable Integer propertyId) {
        return ResponseEntity.created(URI.create("")).body(
                dealerService.listAllApplicationsByPropertyId(propertyId)
        );
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationResponse> viewApplication(@PathVariable Integer applicationId) {
        return ResponseEntity.created(URI.create("")).body(
                dealerService.viewApplication(applicationId)
        );
    }

    @PostMapping("/applications/approve/{applicationId}")
    public ResponseEntity<String> approveApplication(@PathVariable Integer applicationId) {
        this.dealerService.approveApplication(applicationId);
        return ResponseEntity.ok("Application approved");
    }

    @PostMapping("/applications/reject/{applicationId}")
    public ResponseEntity<String> rejectApplication(@PathVariable Integer applicationId) {
        this.dealerService.rejectApplication(applicationId);
        return ResponseEntity.ok("Application rejected");
    }

    @PostMapping("/applications/unread/{applicationId}")
    public ResponseEntity<String> unreadApplication(@PathVariable Integer applicationId) {
        this.dealerService.unreadApplication(applicationId);
        return ResponseEntity.ok("Application marked as unread");
    }
}
