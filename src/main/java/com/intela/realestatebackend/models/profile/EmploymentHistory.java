package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JsonBackReference
    private CustomerInformation profile;

    @ManyToOne
    @JoinColumn(name = "reference_id")
    @JsonIgnore
    @JsonBackReference
    private Reference reference;
}
