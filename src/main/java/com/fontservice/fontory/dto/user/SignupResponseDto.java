package com.fontservice.fontory.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponseDto {
    private Integer status;   // 200 or 500
    private String message;
}
