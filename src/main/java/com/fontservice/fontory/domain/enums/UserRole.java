package com.fontservice.fontory.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    USER, ADMIN;

    @JsonValue
    public String toLower() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static UserRole from(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }

}
