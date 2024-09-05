package com.intela.realestatebackend.services.data;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.profile.ApplicationStatus;
import com.intela.realestatebackend.models.property.Application;
import com.intela.realestatebackend.models.property.Bookmark;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.repositories.BookmarkRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.repositories.application.ApplicationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@DependsOn("bookmarkDataLoader")
public class ApplicationDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Transactional
    @PostConstruct
    public void submitApplicationsForCustomers() {
        // Step 1: Retrieve all users with the role CUSTOMER
        List<User> customers = userRepository.findAllByRole(Role.CUSTOMER).orElseThrow(
                () -> new IllegalArgumentException("Invalid role")
        );

        // Step 2: Iterate over each customer
        for (User customer : customers) {
            // Step 3: Retrieve bookmarks for each customer
            List<Bookmark> bookmarks = bookmarkRepository.findAllByUserId(customer.getId(), Pageable.ofSize(1));

            if (!bookmarks.isEmpty()) {
                // Step 4: Get the first bookmarked property
                Property bookmarkedProperty = bookmarks.get(0).getProperty();

                // Step 5: Create a new Application for the bookmarked property
                Application application = Application.builder()
                        .property(bookmarkedProperty)
                        .status(ApplicationStatus.UNREAD)  // Assuming SUBMITTED is a valid status
                        .submittedDate(new Date(System.currentTimeMillis()))
                        .build();

                // Step 6: Save the application to the database
                applicationRepository.save(application);
            }
        }
    }
}
