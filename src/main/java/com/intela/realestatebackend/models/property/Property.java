package com.intela.realestatebackend.models.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.BillType;
import com.intela.realestatebackend.models.archetypes.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String propertyOwnerName;
    private String location;
    private String description;
    private Integer numberOfRooms;
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;
    private String status;
    private Long price;
    @Enumerated(EnumType.STRING)
    private BillType billType;
    private Timestamp availableFrom;
    private Timestamp availableTill;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "property",
            orphanRemoval = true
    )
    @ToString.Exclude
    @JsonManagedReference("property-propertyImages")
    private List<PropertyImage> propertyImages = new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "property",
            orphanRemoval = true
    )
    @JsonManagedReference("property-bookmarks")
    @Schema(hidden = true)
    private Set<Bookmark> bookmarks;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "property",
            orphanRemoval = true
    )
    @JsonManagedReference("property-applications")
    @Schema(hidden = true)
    private Set<Application> applications;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    @JsonBackReference("user-properties")
    private User user;

    // Self-referencing relationship for parent and child properties
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_listing", referencedColumnName = "id")
    @Schema(hidden = true)
    @JsonBackReference("property-plans")
    private Property parentListing; // Parent property (main property)

    @OneToMany(mappedBy = "parentListing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("property-plans")
    private Set<Plan> plans = new HashSet<>();
}
