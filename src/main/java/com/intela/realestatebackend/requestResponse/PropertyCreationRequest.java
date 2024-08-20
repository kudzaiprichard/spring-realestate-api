package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Property;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class PropertyCreationRequest extends Property {
}
