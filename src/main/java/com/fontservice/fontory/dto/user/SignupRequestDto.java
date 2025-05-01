package com.fontservice.fontory.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @Schema(description = "사용자 ID", example = "id1234")
    private String userId;

    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    @Schema(description = "비밀번호 확인", example = "password1234")
    private String passwordConfirm;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "이메일", example = "gildong@example.com")
    private String email;

    @Schema(description = "닉네임", example = "길동이")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
    private String profileImage;
}
