package com.fontservice.fontory.dto.font;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FontDetailResponse {
    private Integer fontId;
    private String fontName;

    private String creatorId;
    private String creatorNickname;
    private String creatorProfileImage;

    private int likeCount;
    private int downloadCount;
    private String ttfUrl;
    private String otfUrl;
    private String description;
    private String originalImageUrl;
    private String createdAt;

    private Boolean liked; // 로그인 유저의 좋아요 여부
}
