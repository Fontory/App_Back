// src/main/java/com/fontservice/fontory/dto/user/UserProfileDto.java
package com.fontservice.fontory.dto.user;

import com.fontservice.fontory.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDto {
    private String userId;
    private String nickname;
    private String profileImage;

    public static UserProfileDto from(User user) {
        return new UserProfileDto(
            user.getUserId(),
            user.getNickname(),
            user.getProfileImage()
        );
    }
}
