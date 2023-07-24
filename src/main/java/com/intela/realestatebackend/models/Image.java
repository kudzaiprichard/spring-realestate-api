package com.intela.realestatebackend.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "images")
public class Image{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Lob
    @Column(length = 400000000)
    private byte[] image;
    private String type;
    private String name;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @ToString.Exclude
    private Property property;
}
