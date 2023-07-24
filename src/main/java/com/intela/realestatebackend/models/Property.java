package com.intela.realestatebackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String propertyOwnerName;
    private String location;
    private String description;
    private Integer numberOfRooms;
    private String propertyType;
    private String status;
    private Long price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feature_id")
    private Feature feature;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "property"
    )
    @ToString.Exclude
    private List<Image> images = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user;
}
