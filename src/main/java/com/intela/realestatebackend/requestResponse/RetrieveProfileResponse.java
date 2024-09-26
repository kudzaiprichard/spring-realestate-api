package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intela.realestatebackend.models.profile.ID;
import com.intela.realestatebackend.models.profile.Profile;
import com.intela.realestatebackend.models.property.PropertyImage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class RetrieveProfileResponse extends Profile {
    private Integer userId;
    @JsonIgnore
    private Set<ID> ids = new HashSet<>();

    public RetrieveProfileResponse(Profile profile) {
        BeanUtils.copyProperties(profile, this);
        init();
    }

    private void init() {
        this.userId = this.getProfileOwner() != null ? this.getProfileOwner().getId() : null;
    }
}
