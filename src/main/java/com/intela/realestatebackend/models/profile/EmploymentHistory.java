package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employment_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmploymentHistory {

    @Id
    @Schema(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employerName;
    private int years;
    private int months;
    private double monthlySalary;
    private String screeningQuestion;
    private String applicantResponse;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @Schema(hidden = true)
    @JsonBackReference("profile-employmentHistories")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "reference_id")
    @Schema(hidden = true)
    @JsonBackReference("reference-employmentHistories")
    private Reference reference;
}
