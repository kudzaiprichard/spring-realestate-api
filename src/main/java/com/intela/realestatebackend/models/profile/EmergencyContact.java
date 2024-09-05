package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emergency_contacts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmergencyContact {

    @Id
    @Schema(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String relationship;
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @Schema(hidden = true)
    @JsonBackReference("profile-emergencyContacts")
    private Profile profile;
}
