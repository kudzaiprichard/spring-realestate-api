package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.property.Property;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class PropertyResponse extends Property {
}
