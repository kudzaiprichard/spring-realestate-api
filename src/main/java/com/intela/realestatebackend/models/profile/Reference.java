package com.intela.realestatebackend.models.profile;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "user_references")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String relationship;
    private String contactNumber;
    private String contactEmail;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @Schema(hidden = true)
    @JsonBackReference("profile-references")
    private Profile profile;

    @OneToMany(mappedBy = "reference")
    @JsonManagedReference("reference-residentialHistories")
    private Set<ResidentialHistory> residentialHistories;

    @OneToMany(mappedBy = "reference")
    @JsonManagedReference("reference-employmentHistories")
    private Set<EmploymentHistory> employmentHistories;
}
