package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.ProfileImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageResponse extends ProfileImage {
    private Integer userId;

    public ProfileImageResponse(ProfileImage image) {
        BeanUtils.copyProperties(image, this);
        init();
    }

    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
    }
}
