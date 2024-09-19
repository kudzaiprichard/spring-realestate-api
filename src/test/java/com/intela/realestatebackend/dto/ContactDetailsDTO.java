package com.intela.realestatebackend.dto;

import com.intela.realestatebackend.models.profile.ContactDetails;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@Data
public class ContactDetailsDTO extends ContactDetails {
}
