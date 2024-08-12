package com.intela.realestatebackend.models.application;

import com.intela.realestatebackend.models.Property;
import com.intela.realestatebackend.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @OneToMany(mappedBy = "profile")
    private Set<ContactDetails> contactDetails;

    @OneToMany(mappedBy = "profile")
    private Set<EmergencyContact> emergencyContacts;

    @OneToMany(mappedBy = "profile")
    private Set<ResidentialHistory> residentialHistories;

    @OneToMany(mappedBy = "profile")
    private Set<EmploymentHistory> employmentHistories;

    @OneToMany(mappedBy = "profile")
    private Set<PersonalDetails> personalDetails;

    @OneToMany(mappedBy = "profile")
    private Set<ID> ids;

    @OneToMany(mappedBy = "profile")
    private Set<Reference> references;
}
