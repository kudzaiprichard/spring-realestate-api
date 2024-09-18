package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intela.realestatebackend.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
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
    private Set<ID> ids = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-references")
    private Set<Reference> references;

    @PrePersist
    @PreUpdate
    public void setRelationships() {
        // Set bidirectional relationship for ContactDetails
        if (this.getContactDetails() != null) {
            this.getContactDetails().setProfile(this);
        }

        // Set bidirectional relationship for EmergencyContacts
        if (this.getEmergencyContacts() != null) {
            this.getEmergencyContacts().forEach(contact -> contact.setProfile(this));
        }

        // Set bidirectional relationship for ResidentialHistories
        if (this.getResidentialHistories() != null) {
            this.getResidentialHistories().forEach(history -> history.setProfile(this));
        }

        // Set bidirectional relationship for EmploymentHistories
        if (this.getEmploymentHistories() != null) {
            this.getEmploymentHistories().forEach(history -> history.setProfile(this));
        }

        // Set bidirectional relationship for PersonalDetails
        if (this.getPersonalDetails() != null) {
            this.getPersonalDetails().forEach(detail -> detail.setProfile(this));
        }

        // Set bidirectional relationship for IDs
        if (this.getIds() != null) {
            this.getIds().forEach(id -> id.setProfile(this));
        }

        // Set bidirectional relationship for References
        if (this.getReferences() != null) {
            this.getReferences().forEach(reference -> reference.setProfile(this));
        }
    }
}
