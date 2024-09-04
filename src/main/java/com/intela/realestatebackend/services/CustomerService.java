package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.property.Application;
import com.intela.realestatebackend.models.property.Bookmark;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.repositories.CustomerInformationRepository;
import com.intela.realestatebackend.repositories.application.ApplicationRepository;
import com.intela.realestatebackend.requestResponse.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.intela.realestatebackend.util.Util.*;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CustomerInformationRepository customerInformationRepository;
    private final JwtService jwtService;
    private final ApplicationRepository applicationRepository;
    public List<BookmarkResponse> fetchAllBookmarksByUserId(HttpServletRequest servletRequest, Pageable pageRequest){
        User loggedUser = getUserByToken(servletRequest, jwtService, this.userRepository);
        List<Bookmark> bookmarks = this.bookmarkRepository.findAllByUserId(loggedUser.getId(), pageRequest);
        List<BookmarkResponse> bookmarkResponses = new ArrayList<>();

        bookmarks.forEach(BookmarkResponse::new);

        return bookmarkResponses;
    }

    public BookmarkResponse fetchBookmarkById(Integer bookmarkId, HttpServletRequest servletRequest){
        User loggedUser = getUserByToken(servletRequest, jwtService, this.userRepository);
        Bookmark bookmark = this.bookmarkRepository.findById(bookmarkId)
            .orElseThrow(() -> new EntityNotFoundException("Bookmark not found"));
        if (bookmark.getUser().getId() == loggedUser.getId())
            return new BookmarkResponse(bookmark);
        else
            return null;
    }

    public void addBookmark(Integer propertyId, HttpServletRequest servletRequest){

        if(this.bookmarkRepository.findByPropertyId(propertyId) != null){
            throw new RuntimeException("Property already exists in your bookmarks");
        }

        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        Property property = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .property(property)
                .build();
        this.bookmarkRepository.save(bookmark);
    }

    public void removeBookmark(Integer bookmarkId, HttpServletRequest servletRequest) {
        // Retrieve the user by token from the request
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Find the bookmark by its ID
        Bookmark bookmark = this.bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found"));

        // Check if the bookmark belongs to the user
        if (!bookmark.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to remove this bookmark");
        }

        // Remove the bookmark from the repository
        this.bookmarkRepository.delete(bookmark);
    }

    public void createApplication(Integer propertyId, HttpServletRequest servletRequest, ApplicationRequest request) {
        // Retrieve the user by token from the request
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Find the property by its ID
        Property property = this.propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        // Create a new CustomerInformation entity using the request data
        Application application = Application.builder()
                .user(user)
                .property(property)
                .contactDetails(request.getContactDetails())
                .emergencyContacts(request.getEmergencyContacts())
                .residentialHistories(request.getResidentialHistories())
                .employmentHistories(request.getEmploymentHistories())
                .personalDetails(request.getPersonalDetails())
                .ids(request.getIds())
                .references(request.getReferences())
                .build();

        // Save the CustomerInformation entity
        this.customerInformationRepository.save(application);
    }

    public List<ApplicationResponse> getAllApplications(HttpServletRequest servletRequest) {
        // Retrieve the user by token from the request
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Fetch all CustomerInformation entities for the user where propertyId is not null
        List<Application> applications = this.applicationRepository.findByUser(user);

        // Filter out entries where property is null
        List<ApplicationResponse> applicationResponses = applications.stream()
                .filter(info -> info.getProperty() != null)
                .map(ApplicationResponse::new)
                .collect(Collectors.toList());

        return applicationResponses;
    }

    public ApplicationResponse getApplication(Integer applicationId, HttpServletRequest servletRequest) {
        // Retrieve the user by token from the request
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Find the CustomerInformation by its ID
        Application application = this.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // Check if the CustomerInformation belongs to the user
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this application");
        }

        ApplicationResponse response = mapApplicationToApplicationResponse(application);

        // Return the ApplicationResponse
        return response;
    }

    public void withdrawApplication(Integer applicationId, HttpServletRequest servletRequest) {
        // Retrieve the user by token from the request
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);

        // Find the CustomerInformation by its ID
        Application application = this.applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // Check if the CustomerInformation belongs to the user
        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to withdraw this application");
        }

        // Delete the CustomerInformation (withdraw the application)
        this.customerInformationRepository.delete(application);
    }
}
