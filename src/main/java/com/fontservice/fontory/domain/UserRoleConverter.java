package com.fontservice.fontory.domain;

import com.fontservice.fontory.domain.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole role) {
        return role == null ? null : role.name().toLowerCase();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return dbData == null ? null : UserRole.valueOf(dbData.toUpperCase());
    }
}