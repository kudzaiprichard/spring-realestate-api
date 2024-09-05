package com.intela.realestatebackend.models.property;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.intela.realestatebackend.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Entity(name = "bookmarks")
public class Bookmark {
    @Id
    @Schema(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    @JsonBackReference("user-bookmarks")
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id")
    @Schema(hidden = true)
    @JsonBackReference("property-bookmarks")
    private Property property;
}
