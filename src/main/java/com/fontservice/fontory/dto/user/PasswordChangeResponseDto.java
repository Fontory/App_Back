package com.fontservice.fontory.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PasswordChangeResponseDto {
    private Integer status;
    private String message;
}
