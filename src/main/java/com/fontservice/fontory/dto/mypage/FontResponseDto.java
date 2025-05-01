package com.fontservice.fontory.dto.mypage;

import com.fontservice.fontory.domain.Font;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FontResponseDto {
    private final Integer fontId;
    private final String name;
    private final String otfUrl;
    private final String ttfUrl;
    private final String description;
    private final String originalImageUrl;
    private final String isPublic;
    private final Integer likeCount;
    private final Integer downloadCount;
    private final LocalDateTime createdAt;

    public FontResponseDto(Font font) {
        this.fontId = font.getFontId();
        this.name = font.getName();
        this.otfUrl = font.getOtfUrl();
        this.ttfUrl = font.getTtfUrl();
        this.description = font.getDescription();
        this.originalImageUrl = font.getOriginalImageUrl();
        this.isPublic = font.getIsPublic().name();
        this.likeCount = font.getLikeCount();
        this.downloadCount = font.getDownloadCount();
        this.createdAt = font.getCreatedAt();
    }
}
