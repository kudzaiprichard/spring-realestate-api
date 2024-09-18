package com.intela.realestatebackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.intela.realestatebackend.models.archetypes.Role;
import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.models.property.Application;
import com.intela.realestatebackend.models.property.Bookmark;
import com.intela.realestatebackend.models.property.Property;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    @Column(unique = true)
    private String email;
    private String password;
    private Timestamp bannedTill;
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-tokens")
    private List<Token> tokens;

    @OneToMany(
            cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user"
    )
    @JsonManagedReference("user-bookmarks")
    @Schema(hidden = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(
            cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user"
    )
    @JsonManagedReference("user-properties")
    @Schema(hidden = true)
    private List<Property> properties = new ArrayList<>();

    @OneToOne(mappedBy = "profileOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-profile")
    @Schema(hidden = true)
    private Profile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-profileImage")
    private ProfileImage profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-applications")
    @Schema(hidden = true)
    private Set<Application> applications;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @PrePersist
    @PreUpdate
    public void setRelationships() {
        // Set bidirectional relationship for ContactDetails
        if (this.getProfile() != null) {
            this.getProfile().setProfileOwner(this);
        }

    }
}
