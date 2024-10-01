package com.intela.realestatebackend.services.data;

import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.profile.*;
import com.intela.realestatebackend.repositories.ProfileRepository;
import com.intela.realestatebackend.repositories.UserRepository;
import com.intela.realestatebackend.repositories.application.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ContactDetailsRepository contactDetailsRepository;

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
    private ResidentialHistoryRepository residentialHistoryRepository;

    @Autowired
    private EmploymentHistoryRepository employmentHistoryRepository;

    @Autowired
    private PersonalDetailsRepository personalDetailsRepository;

    @Autowired
    private IDRepository idRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Transactional
    @PostConstruct
    public void loadCustomers() {
        for (int i = 1; i <= 5; i++) {
            // Create a new ContactDetails instance
            ContactDetails contactDetails = ContactDetails.builder()
                    .contactEmail("contact" + i + "@example.com")
                    .contactNumber("123-456-789" + i)
                    .build();

            // Create a few EmergencyContact instances
            Set<EmergencyContact> emergencyContacts = new HashSet<>();
            for (int j = 1; j <= 2; j++) {
                EmergencyContact emergencyContact = EmergencyContact.builder()
                        .firstName("Emergency")
                        .lastName("Contact " + j)
                        .contactNumber("987-654-321" + j)
                        .build();
                emergencyContacts.add(emergencyContact);
            }

            // Create a few ResidentialHistory instances
            Set<ResidentialHistory> residentialHistories = new HashSet<>();
            ResidentialHistory residentialHistory = ResidentialHistory.builder()
                    .address("123 Street Name " + i)
                    .years(2)
                    .months(6)
                    .weeklyRent(500.0)
                    .build();
            residentialHistories.add(residentialHistory);
            //Create References
            Set<Reference> references = new HashSet<>();
            Reference reference = new Reference();
            reference.setContactNumber("0435777777");
            reference.setContactEmail("my.reference@gmail.com");
            reference.setFirstName("My");
            reference.setLastName("Reference");
            reference.setResidentialHistories(residentialHistories);
            references.add(reference);
            // Create CustomerInformation and set the relationships
            Profile profile = Profile.builder()
                    .contactDetails(contactDetails)
                    .emergencyContacts(emergencyContacts)
                    .residentialHistories(residentialHistories)
                    .references(references)
                    .build();
            // Create a new User instance
            User user = User.builder()
                    .firstName("FirstName" + i)
                    .lastName("LastName" + i)
                    .email("user" + i + "@example.com")
                    .mobileNumber("123-456-789" + i)
                    .password("password" + i)
                    .role(Role.CUSTOMER)
                    .profile(profile)  // Associate the CustomerInformation with the User
                    .build();

            // Save User to the database
            userRepository.save(user);
        }
    }

    @Transactional
    @PostConstruct
    public void loadDealerUsers() {
        for (int i = 1; i <= 5; i++) {
            // Create a new ContactDetails instance
            ContactDetails contactDetails = ContactDetails.builder()
                    .contactEmail("dealer_contact" + i + "@example.com")
                    .contactNumber("456-789-123" + i)
                    .build();

            // Create a few EmergencyContact instances
            Set<EmergencyContact> emergencyContacts = new HashSet<>();
            for (int j = 1; j <= 2; j++) {
                EmergencyContact emergencyContact = EmergencyContact.builder()
                        .firstName("Dealer Emergency FirstName " + j)
                        .lastName("Dealer Emergency LastName " + j)
                        .contactNumber("654-321-987" + j)
                        .build();
                emergencyContacts.add(emergencyContact);
            }

            // Create a few ResidentialHistory instances
            Set<ResidentialHistory> residentialHistories = new HashSet<>();
            ResidentialHistory residentialHistory = ResidentialHistory.builder()
                    .address("456 Dealer Street " + i)
                    .years(3)
                    .months(4)
                    .weeklyRent(600.0)
                    .build();
            residentialHistories.add(residentialHistory);

            // Create CustomerInformation and set the relationships
            Profile profile = Profile.builder()
                    .contactDetails(contactDetails)
                    .emergencyContacts(emergencyContacts)
                    .residentialHistories(residentialHistories)
                    .build();

            // Create a new User instance with Role.DEALER
            User user = User.builder()
                    .firstName("DealerFirstName" + i)
                    .lastName("DealerLastName" + i)
                    .email("dealer" + i + "@example.com")
                    .mobileNumber("456-789-123" + i)
                    .password("dealerPassword" + i)
                    .role(Role.DEALER)  // Set the role as Role.DEALER
                    .profile(profile)  // Associate the CustomerInformation with the User
                    .build();

            // Save User to the database
            userRepository.save(user);
        }
    }

    @Transactional
    @PostConstruct
    public void loadAdminUsers() {
        for (int i = 1; i <= 5; i++) {
            // Create a new ContactDetails instance
            ContactDetails contactDetails = ContactDetails.builder()
                    .contactEmail("admin_contact" + i + "@example.com")
                    .contactNumber("789-123-456" + i)
                    .build();

            // Create a few EmergencyContact instances
            Set<EmergencyContact> emergencyContacts = new HashSet<>();
            for (int j = 1; j <= 2; j++) {
                EmergencyContact emergencyContact = EmergencyContact.builder()
                        .firstName("Admin Emergency FirstName " + j)
                        .lastName("Admin Emergency LastName " + j)
                        .contactNumber("321-987-654" + j)
                        .build();
                emergencyContacts.add(emergencyContact);
            }

            // Create a few ResidentialHistory instances
            Set<ResidentialHistory> residentialHistories = new HashSet<>();
            ResidentialHistory residentialHistory = ResidentialHistory.builder()
                    .address("789 Admin Street " + i)
                    .years(4)
                    .months(2)
                    .weeklyRent(700.0)
                    .build();
            residentialHistories.add(residentialHistory);

            // Create CustomerInformation and set the relationships
            Profile profile = Profile.builder()
                    .contactDetails(contactDetails)
                    .emergencyContacts(emergencyContacts)
                    .residentialHistories(residentialHistories)
                    .build();

            // Create a new User instance with Role.ADMIN
            User user = User.builder()
                    .firstName("AdminFirstName" + i)
                    .lastName("AdminLastName" + i)
                    .email("admin" + i + "@example.com")
                    .mobileNumber("789-123-456" + i)
                    .password("adminPassword" + i)
                    .role(Role.ADMIN)  // Set the role as Role.ADMIN
                    .profile(profile)  // Associate the CustomerInformation with the User
                    .build();

            // Save User to the database
            userRepository.save(user);
        }
    }
}
