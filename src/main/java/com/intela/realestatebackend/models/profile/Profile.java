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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    @JsonBackReference("user-profile")
    private User profileOwner;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("profile-contactDetails")
    private ContactDetails contactDetails;

    public void setEmergencyContacts(Set<EmergencyContact> emergencyContacts) {
        if(this.emergencyContacts == null){
            this.emergencyContacts = emergencyContacts;
        } else {
            this.emergencyContacts.clear();
            this.emergencyContacts.addAll(emergencyContacts);
        }
    }

    public void setResidentialHistories(Set<ResidentialHistory> residentialHistories) {
        if(this.residentialHistories == null){
            this.residentialHistories = residentialHistories;
        } else {
            this.residentialHistories.clear();
            this.residentialHistories.addAll(residentialHistories);
        }
    }

    public void setEmploymentHistories(Set<EmploymentHistory> employmentHistories) {
        if(this.employmentHistories == null){
            this.employmentHistories = employmentHistories;
        } else {
            this.employmentHistories.clear();
            this.employmentHistories.addAll(employmentHistories);
        }
    }

    public void setPersonalDetails(Set<PersonalDetails> personalDetails) {
        if(this.personalDetails == null){
            this.personalDetails = personalDetails;
        } else {
            this.personalDetails.clear();
            this.personalDetails.addAll(personalDetails);
        }
    }

    public void setIds(Set<ID> ids) {
        if(this.ids == null){
            this.ids = ids;
        } else {
            this.ids.clear();
            this.ids.addAll(ids);
        }
    }

    public void setReferences(Set<Reference> references) {
        if(this.references == null){
            this.references = references;
        } else {
            this.references.clear();
            this.references.addAll(references);
        }
    }

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
