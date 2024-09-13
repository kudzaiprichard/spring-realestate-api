package com.intela.realestatebackend.requestResponse;

import com.intela.realestatebackend.models.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class RetrieveAccountResponse extends User {
    public RetrieveAccountResponse(User user) {
        BeanUtils.copyProperties(user, this);
    }
}
