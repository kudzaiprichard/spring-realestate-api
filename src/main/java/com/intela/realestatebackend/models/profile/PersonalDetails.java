package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(hidden = true)
    private Long id;

    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;

    @OneToOne
    @JoinColumn(name = "profile_id")
    @Schema(hidden = true)
    @JsonBackReference("profile-personalDetails")
    private Profile profile;
}
