package com.fontservice.fontory.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private Integer status;       // "200" or "500"
    private String message;      // 실패 메시지 or null
    private UserInfoDto user;    // 로그인 성공 시 유저 정보

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfoDto {
        private String userId;
        private String nickname;
    }
}
