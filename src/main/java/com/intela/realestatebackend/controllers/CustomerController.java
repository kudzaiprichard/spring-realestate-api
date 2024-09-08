package com.intela.realestatebackend.controllers;

import com.intela.realestatebackend.requestResponse.ApplicationRequest;
import com.intela.realestatebackend.requestResponse.ApplicationResponse;
import com.intela.realestatebackend.requestResponse.BookmarkResponse;
import com.intela.realestatebackend.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/bookmarks/add/{propertyId}")
    public ResponseEntity<String> addBookmark(@PathVariable Integer propertyId, HttpServletRequest servletRequest) {
        this.customerService.addBookmark(propertyId, servletRequest);
        return ResponseEntity.ok("Bookmark successfully added");
    }

    @DeleteMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<String> removeBookmark(@PathVariable Integer bookmarkId, HttpServletRequest servletRequest) {
        this.customerService.removeBookmark(bookmarkId, servletRequest);
        return ResponseEntity.ok("Bookmark successfully removed");
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<List<BookmarkResponse>> fetchAllBookmarksByUserId(
            HttpServletRequest servletRequest,
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
        return ResponseEntity.ok(this.customerService.fetchAllBookmarksByUserId(servletRequest, pageRequest));
    }

    @GetMapping("/bookmarks/{bookmarkId}")
    public ResponseEntity<BookmarkResponse> fetchBookmarkById(@PathVariable Integer bookmarkId, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.customerService.fetchBookmarkById(bookmarkId, servletRequest));
    }

    @Operation(
            summary = "Upload application data and images",
            description = "Uploads an application JSON and associated image files",
            requestBody = @RequestBody(
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
                                            schema = @Schema(implementation = ApplicationRequest.class)
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
    @PostMapping(value = "/applications/create/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createApplication(@PathVariable Integer propertyId, HttpServletRequest servletRequest,
                                                    @RequestPart("request") ApplicationRequest request, @RequestPart(name = "images", required = false) MultipartFile[] images) {
        this.customerService.createApplication(propertyId, servletRequest, request, images);
        return ResponseEntity.ok("Application created");
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications(HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.customerService.getAllApplications(servletRequest));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable Integer applicationId, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(this.customerService.getApplication(applicationId, servletRequest));
    }

    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<String> withdrawApplication(@PathVariable Integer applicationId, HttpServletRequest servletRequest) {
        this.customerService.withdrawApplication(applicationId, servletRequest);
        return ResponseEntity.ok("Application successfully withdrawn");
    }

}
