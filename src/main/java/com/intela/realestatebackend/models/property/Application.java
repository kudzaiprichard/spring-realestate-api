package com.intela.realestatebackend.models.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.intela.realestatebackend.models.profile.ApplicationStatus;
import com.intela.realestatebackend.models.profile.CustomerInformation;
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
public class Application extends CustomerInformation {
    @ManyToOne
    @JoinColumn(name = "property_id")
    @JsonBackReference
    private Property property;

    private ApplicationStatus status;

    private Date submittedDate;
}
