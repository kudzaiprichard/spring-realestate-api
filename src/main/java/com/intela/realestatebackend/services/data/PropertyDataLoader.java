package com.intela.realestatebackend.services.data;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.BillType;
import com.intela.realestatebackend.models.archetypes.PropertyType;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.property.Feature;
import com.intela.realestatebackend.models.property.Plan;
import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.repositories.PropertyRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@DependsOn("userDataLoader")
public class PropertyDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    public void addPropertiesToDealers() {
        // Fetch all users with the role DEALER
        List<User> dealers = userRepository.findAllByRole(Role.DEALER).orElseThrow(
                () -> new IllegalArgumentException("Invalid role")
        );

        // Iterate over each dealer and add three properties
        for (User dealer : dealers) {
            for (int i = 1; i <= 3; i++) {
                Feature feature = new Feature();
                feature.setBathrooms(2);
                feature.setParking(1);
                feature.setLounges(1);
                feature.setBedrooms(3);
                // Create a new Property instance
                Property property = Property.builder()
                        .propertyOwnerName(dealer.getFirstName() + " " + dealer.getLastName())
                        .location("Dealer Street " + i)
                        .description("Property " + i + " owned by " + dealer.getFirstName())
                        .numberOfRooms(3 + i)  // Dummy number of rooms
                        .propertyType(PropertyType.HOUSE)  // Assuming PropertyType.HOUSE is defined
                        .status("Available")
                        .feature(feature)
                        .price(500000L + (i * 10000L))  // Dummy price
                        .billType(BillType.INCLUDED)  // Assuming BillType.MONTHLY is defined
                        .availableFrom(Timestamp.from(Instant.now()))
                        .availableTill(Timestamp.from(Instant.now().plusSeconds(86400L * 30)))  // Available for 30 days
                        .user(dealer)  // Associate the property with the dealer
                        .build();

                // Save the property to the database
                propertyRepository.save(property);
            }
        }
    }

    public void addPlansToPropertiesByDealers() {
        // Step 1: Retrieve all users with the role DEALER
        List<User> dealers = userRepository.findAllByRole(Role.DEALER).orElseThrow(
                () -> new IllegalArgumentException("Invalid role")
        );

        // Step 2: Iterate over each dealer
        for (User dealer : dealers) {
            // Force initialization of the lazy-loaded properties collection
            Hibernate.initialize(dealer.getProperties());
            // Step 3: Get properties of each dealer
            List<Property> properties = dealer.getProperties();

            // Step 4: Iterate over each property
            for (Property property : properties) {
                // Step 5: Add three plans to each property
                for (int i = 1; i <= 3; i++) {
                    Feature feature = new Feature();
                    feature.setBathrooms(1);
                    feature.setBedrooms(1);
                    Plan plan = Plan.builder()
                            .propertyOwnerName(property.getPropertyOwnerName())
                            .user(property.getUser())
                            .location(property.getLocation() + " - Plan " + i)
                            .description("Plan " + i + " for property " + property.getId())
                            .numberOfRooms(property.getNumberOfRooms())
                            .propertyType(PropertyType.ENSUITE)
                            .status("Available")
                            .feature(feature)
                            .price(property.getPrice() + (i * 10000L))  // Adjust price for each plan
                            .billType(property.getBillType())
                            .availableFrom(Timestamp.from(Instant.now()))
                            .availableTill(Timestamp.from(Instant.now().plusSeconds(86400L * 30)))  // Available for 30 days
                            .parentListing(property)  // Associate plan with the parent property
                            .build();

                    // Step 6: Save the plan
                    propertyRepository.save(plan);
                }
            }
        }
    }

    public void loadData() {
        addPropertiesToDealers();
        addPlansToPropertiesByDealers();
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadData();
    }
}
