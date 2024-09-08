package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "contact_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDetails {

    @Id
    @Schema(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contactNumber;
    private String contactEmail;

    @OneToOne
    @JoinColumn(name = "profile_id")
    @Schema(hidden = true)
    @JsonBackReference("profile-contactDetails")
    private Profile profile;
}
