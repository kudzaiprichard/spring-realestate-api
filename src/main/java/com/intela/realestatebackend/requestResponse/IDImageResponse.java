package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.profile.ID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IDImageResponse extends ID {
    private Long profileId;

    public IDImageResponse(ID id) {
        BeanUtils.copyProperties(id, this);
        init();
    }

    private void init() {
        this.profileId = this.getProfile() != null ? this.getProfile().getId() : null;
    }
}
