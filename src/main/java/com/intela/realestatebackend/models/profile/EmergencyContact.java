package com.intela.realestatebackend.models.profile;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String relationship;
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CustomerInformation profile;
}
