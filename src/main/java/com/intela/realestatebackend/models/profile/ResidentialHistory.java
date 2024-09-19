package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "residential_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResidentialHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private String address;
    private int years;
    private int months;
    private double weeklyRent;
    private String screeningQuestion;
    private String applicantResponse;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @Schema(hidden = true)
    @JsonBackReference("profile-residentialHistories")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "reference_id")
    @Schema(hidden = true)
    @JsonBackReference("reference-residentialHistories")
    private Reference reference;
}
