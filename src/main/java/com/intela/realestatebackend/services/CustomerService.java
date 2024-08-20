package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.property.Bookmark;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.PropertyImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.ApplicationRequest;
import com.intela.realestatebackend.requestResponse.ApplicationResponse;
import com.intela.realestatebackend.requestResponse.ImageResponse;
import com.intela.realestatebackend.requestResponse.PropertyResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.intela.realestatebackend.util.Util.*;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtService jwtService;

    //Todo: Add a search/filter/sorting functionality on fetching all filters

    //should include pagination etc
    public List<PropertyResponse> fetchAllProperties(Pageable pageRequest){
        List<PropertyResponse> propertyResponses = new ArrayList<>();

        this.propertyRepository.findAll(pageRequest)
                .forEach(property -> {
                    List<String> imageResponses = new ArrayList<>();
                    property.getPropertyImages().forEach(image1 -> imageResponses.add(image1.getName()));
                    propertyResponses.add(getPropertyResponse(imageResponses, property, userRepository));
                });

        return propertyResponses;
    }

    public PropertyResponse fetchPropertyById(Integer propertyId){
        return getPropertyById(propertyId, this.propertyRepository, this.userRepository);
    }

    public List<ImageResponse> fetchAllImagesByPropertyId(int propertyId) {
        return getImageByPropertyId(propertyId, this.propertyImageRepository);
    }

    public List<PropertyResponse> fetchAllBookmarksByUserId(HttpServletRequest servletRequest, Pageable pageRequest){
        User loggedUser = getUserByToken(servletRequest, jwtService, this.userRepository);
        List<Bookmark> bookmarks = this.bookmarkRepository.findAllByUserId(loggedUser.getId(), pageRequest);
        List<PropertyResponse> bookmarkResponses = new ArrayList<>();

        bookmarks.forEach(bookmark -> bookmarkResponses.add(
                getPropertyById(
                        bookmark.getProperty().getId(),
                        this.propertyRepository,
                        this.userRepository
                    )
                )
        );

        return bookmarkResponses;
    }

    public PropertyResponse fetchBookmarkById(Integer bookmarkId, HttpServletRequest servletRequest){
        User loggedUser = getUserByToken(servletRequest, jwtService, this.userRepository);
        Bookmark bookmark = this.bookmarkRepository.findById(bookmarkId)
            .orElseThrow(() -> new EntityNotFoundException("Bookmark not found"));
        if (bookmark.getUser().getId() == loggedUser.getId())
            return getPropertyById(bookmark.getProperty().getId(), this.propertyRepository, userRepository);
        else
            return null;
    }

    public String addBookmark(Integer propertyId, HttpServletRequest servletRequest){

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
        return "Bookmark added successfully";
    }

    public String removeBookmark(Integer bookmarkId, HttpServletRequest servletRequest) {
        return null;
    }

    public ApplicationResponse createApplication(Integer propertyId, HttpServletRequest servletRequest, ApplicationRequest request) {
        return null;
    }

    public List<ApplicationResponse> getAllApplications(HttpServletRequest servletRequest) {
        return null;
    }

    public List<ApplicationResponse> getApplication(Integer applicationId, HttpServletRequest servletRequest) {
        return null;
    }

    public String withdrawApplication(Integer applicationId, HttpServletRequest servletRequest) {
        return null;
    }
}
