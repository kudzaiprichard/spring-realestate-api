package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.profile.Profile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class RetrieveProfileResponse extends Profile {
    private Integer userId;

    public RetrieveProfileResponse(Profile profile) {
        BeanUtils.copyProperties(profile, this);
        init();
    }

    private void init() {
        this.userId = this.getProfileOwner() != null ? this.getProfileOwner().getId() : null;
    }
}
