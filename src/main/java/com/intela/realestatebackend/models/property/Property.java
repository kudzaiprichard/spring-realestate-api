package com.intela.realestatebackend.models.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.BillType;
import com.intela.realestatebackend.models.archetypes.PropertyType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "properties")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String propertyOwnerName;
    private String location;
    private String description;
    private Integer numberOfRooms;
    private PropertyType propertyType;
    private String status;
    private Long price;
    private BillType billType;
    private Timestamp availableFrom;
    private Timestamp availableTill;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "feature_id")
    @JsonManagedReference
    private Feature feature;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "property",
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonManagedReference
    private List<PropertyImage> propertyImages = new ArrayList<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "property",
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Bookmark> bookmarks;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "property",
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Application> applications;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "parentListing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Plan> plans = new HashSet<>();
}
