package com.fontservice.fontory.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDto {

    @Schema(description = "현재 비밀번호", example = "old1234")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "new1234")
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", example = "new1234")
    private String newPasswordConfirm;
}
