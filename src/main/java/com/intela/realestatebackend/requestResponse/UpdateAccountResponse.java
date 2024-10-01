package com.intela.realestatebackend.requestResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountResponse {
    Map<String, Object> updatedFields;
}
