package com.fontservice.fontory.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindUserIdRequestDto {

    @Schema(description = "이메일", example = "gildong@example.com")
    private String email;
}
