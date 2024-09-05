package com.intela.realestatebackend.services.data;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.property.Bookmark;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@DependsOn("propertyDataLoader")
public class BookmarkDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Transactional
    @PostConstruct
    public void addBookmarksForCustomers() {
        // Step 1: Retrieve all users with the role CUSTOMER
        List<User> customers = userRepository.findAllByRole(Role.CUSTOMER).orElseThrow(
                () -> new IllegalArgumentException("Invalid role")
        );

        // Step 2: Retrieve all properties
        List<Property> properties = propertyRepository.findAll();

        // Step 3: Iterate over each customer and each property
        for (User customer : customers) {
            for (Property property : properties) {
                // Step 4: Create a new Bookmark
                Bookmark bookmark = Bookmark.builder()
                        .user(customer)
                        .property(property)
                        .build();

                // Step 5: Save the Bookmark
                bookmarkRepository.save(bookmark);
            }
        }
    }
}
