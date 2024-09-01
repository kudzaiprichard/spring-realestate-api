package com.intela.realestatebackend.models.profile;

import com.intela.realestatebackend.models.Image;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ids")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ID extends Image {
    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CustomerInformation profile;
}
