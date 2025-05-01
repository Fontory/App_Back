package com.fontservice.fontory.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeResponseDto {
    private String name;
    private String description;
    private int requiredPostCount;
    private String iconUrl;
}
