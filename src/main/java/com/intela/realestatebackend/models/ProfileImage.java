package com.intela.realestatebackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "profile_images")
public class ProfileImage extends Image {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @Schema(hidden = true)
    @JsonBackReference("user-profileImage")
    private User user;
}
