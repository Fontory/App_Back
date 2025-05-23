// src/main/java/com/fontservice/fontory/dto/mypage/DownloadedFontResponseDto.java
package com.fontservice.fontory.dto.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadedFontResponseDto {
    private Integer fontId;
    private String name;
    private String description;
    private String otfUrl;
    private String ttfUrl;
    private String originalImageUrl;
    private String isPublic;
    private String creatorId;
    private String creatorProfileImage;
    private int likeCount;
    private int downloadCount;
    private String createdAt;
}
