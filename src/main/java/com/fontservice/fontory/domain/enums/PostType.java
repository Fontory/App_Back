package com.fontservice.fontory.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PostType {
    TRANSCRIPTION,
    GENERAL;

    @JsonValue
    public String toLower() {
        return this.name().toLowerCase();
    }

    @JsonCreator
    public static PostType from(String value) {
        return PostType.valueOf(value.toUpperCase());
    }
}
