package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intela.realestatebackend.models.profile.ID;
import com.intela.realestatebackend.models.property.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ApplicationResponse extends Application {
    private Integer userId;
    private Integer propertyId;
    @JsonIgnore
    private Set<ID> ids = new HashSet<>();

    public ApplicationResponse(Application application) {
        BeanUtils.copyProperties(application, this);
        init();
    }

    private void init() {
        this.userId = this.getUser() != null ? this.getUser().getId() : null;
        this.propertyId = this.getProperty() != null ? this.getProperty().getId() : null;
    }
}
