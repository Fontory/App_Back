package com.fontservice.fontory.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDto {
    private String userId;
    private String email;
    private String newPassword;
}
