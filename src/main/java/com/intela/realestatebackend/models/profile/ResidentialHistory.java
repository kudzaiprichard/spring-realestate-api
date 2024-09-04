package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Long id;

    private String address;
    private int years;
    private int months;
    private double weeklyRent;
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
