package com.intela.realestatebackend.requestResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intela.realestatebackend.models.profile.ID;
import com.intela.realestatebackend.models.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
@AllArgsConstructor
public class UpdateProfileRequest extends Profile {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<ID> ids;
}
