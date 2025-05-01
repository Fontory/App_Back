package com.fontservice.fontory.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @Schema(description = "사용자 ID", example = "id1234")
    private String userId;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;
}
