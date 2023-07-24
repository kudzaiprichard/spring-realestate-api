package com.intela.realestatebackend.services;

import com.intela.realestatebackend.models.Bookmark;
import com.intela.realestatebackend.models.Property;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.requestResponse.LoggedUserResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.intela.realestatebackend.util.Util.getUserByToken;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtService jwtService;

    //Todo: Get logged in user via token
    public LoggedUserResponse fetchLoggedinUserBytoken(
            HttpServletRequest request
    ){
        User user = getUserByToken(request, jwtService, this.userRepository);
        return LoggedUserResponse.builder()
                .firstname(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .build();
    }

    //Todo: Fetch all properties
    //should include pagination etc
    public List<Property> fetchAllProperties(){
        return this.propertyRepository.findAll();
    }

    //Todo: Fetch property by property id
    public Property fetchPropertyById(Integer propertyId){
        return this.propertyRepository.findById(propertyId)
                .orElseThrow(()->new EntityNotFoundException("Property not found"));
    }

    //Todo: Fetch all bookmarks by user
    public List<Bookmark> fetchAllBookmarkByUserId(Integer userId){
        return this.bookmarkRepository.findAllByUserId(userId);
    }

    //Todo: Fetch bookmark by bookmark id
    public Bookmark fetchBookmarkById(Integer bookmarkId){
        return this.bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found"));
    }

    //Todo: Add bookmark
    public Bookmark addBookmark(Integer bookmarkId, HttpServletRequest servletRequest){
        User user = getUserByToken(servletRequest, jwtService, this.userRepository);
        Property property = this.propertyRepository.findById(bookmarkId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .property(property)
                .build();
       return this.bookmarkRepository.save(bookmark);
    }

    //Todo: Add a search/filter/sorting functionality on fetching all filters
}
