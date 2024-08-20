package com.intela.realestatebackend.models.application;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personal_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CustomerInformation profile;
}
