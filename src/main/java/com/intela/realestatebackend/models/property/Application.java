package com.intela.realestatebackend.models.property;

import com.intela.realestatebackend.models.profile.ApplicationStatus;
import com.intela.realestatebackend.models.profile.CustomerInformation;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    @OneToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private ApplicationStatus status;

    private Date submittedDate;
}
