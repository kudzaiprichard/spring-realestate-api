package com.intela.realestatebackend.models.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Plan extends Property {

    // The parentListing column will store the ID of the parent Property entity
    @ManyToOne
    @JoinColumn(name = "parent_listing", referencedColumnName = "id")
    @JsonIgnore
    @JsonBackReference
    private Property parentListing;
}