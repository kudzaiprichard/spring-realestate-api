package com.intela.realestatebackend.models.application;

import com.intela.realestatebackend.models.property.Property;
import com.intela.realestatebackend.models.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CustomerInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @OneToOne(mappedBy = "profile")
    private ContactDetails contactDetails;

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
