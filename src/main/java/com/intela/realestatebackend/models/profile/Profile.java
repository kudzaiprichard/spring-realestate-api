package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intela.realestatebackend.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Profile {
    @Id
    @Schema(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    @JsonBackReference("user-profile")
    private User profileOwner;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-contactDetails")
    private ContactDetails contactDetails;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-emergencyContacts")
    private Set<EmergencyContact> emergencyContacts;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-residentialHistories")
    private Set<ResidentialHistory> residentialHistories;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-employmentHistories")
    private Set<EmploymentHistory> employmentHistories;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-personalDetails")
    private Set<PersonalDetails> personalDetails;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-ids")
    private Set<ID> ids;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-references")
    private Set<Reference> references;
}
