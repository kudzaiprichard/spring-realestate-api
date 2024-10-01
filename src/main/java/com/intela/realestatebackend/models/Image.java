package com.intela.realestatebackend.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Integer id;
    @Lob
    @Column(length = 400000000)
    private byte[] image;
    private String type;
    private String name;
    private String path;
}
