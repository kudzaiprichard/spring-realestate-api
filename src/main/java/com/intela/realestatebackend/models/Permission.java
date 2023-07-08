package com.intela.realestatebackend.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    DEALER_READ("dealer:read"),
    DEALER_UPDATE("dealer:update"),
    DEALER_CREATE("dealer:create"),
    DEALER_DELETE("dealer:delete"),

    CUSTOMER_READ("dealer:read"),
    CUSTOMER_UPDATE("dealer:update"),
    CUSTOMER_CREATE("dealer:create"),
    CUSTOMER_DELETE("dealer:delete");

    @Getter
    private final String permission;

}
