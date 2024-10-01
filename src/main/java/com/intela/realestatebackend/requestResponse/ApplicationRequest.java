package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intela.realestatebackend.models.profile.ApplicationStatus;
import com.intela.realestatebackend.models.profile.ID;
import com.intela.realestatebackend.models.property.Application;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest extends Application {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<ID> ids;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ApplicationStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date submittedDate;
}
