package com.intela.realestatebackend.models.application;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ids")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ID {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String formOfId;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}
