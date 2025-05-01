package com.fontservice.fontory.dto.mypage;

import lombok.Getter;

@Getter
public class ProfileUpdateRequestDto {
    private String nickname;
    private String email;
    private String profileImage;
}
