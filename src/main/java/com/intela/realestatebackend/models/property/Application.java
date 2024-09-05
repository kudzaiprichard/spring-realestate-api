package com.intela.realestatebackend.models.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.profile.ApplicationStatus;
import com.intela.realestatebackend.models.profile.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@Entity
@Table(name = "property_applications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Application extends Profile {
    @ManyToOne
    @JoinColumn(name = "property_id")
    @Schema(hidden = true)
    @JsonBackReference("property-applications")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    @JsonBackReference("user-applications")
    private User user;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private Date submittedDate;

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
