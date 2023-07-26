package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.Bookmark;
import com.intela.realestatebackend.models.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.ImageRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
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
    private final ImageRepository imageRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtService jwtService;

    //Todo: Add a search/filter/sorting functionality on fetching all filters

    //should include pagination etc
    public List<PropertyResponse> fetchAllProperties(Pageable pageRequest){
        List<PropertyResponse> propertyResponses = new ArrayList<>();

        this.propertyRepository.findAll(pageRequest)
                .forEach(property -> {
                    List<String> imageResponses = new ArrayList<>();
                    property.getImages().forEach(image1 -> imageResponses.add(image1.getName()));
                    propertyResponses.add(getPropertyResponse(imageResponses, property, userRepository));
                });

        return propertyResponses;
    }

    public PropertyResponse fetchPropertyById(Integer propertyId){
        return getPropertyById(propertyId, this.propertyRepository, this.userRepository);
    }

    public List<ImageResponse> fetchAllImagesByPropertyId(int propertyId) {
        return getImageByPropertyId(propertyId, this.imageRepository);
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

    public PropertyResponse fetchBookmarkById(Integer bookmarkId){
         Bookmark bookmark = this.bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found"));

        return getPropertyById(bookmark.getProperty().getId(), this.propertyRepository, userRepository);
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
}
