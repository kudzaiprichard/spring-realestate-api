package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.application.CustomerInformation;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class ApplicationRequest extends CustomerInformation {
}
