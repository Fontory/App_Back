package com.fontservice.fontory.dto.font;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FontWithUserProfileResponse {
    private Integer fontId;
    private String fontName;
    private String otfUrl;
    private String ttfUrl;
    private String description;
    private String originalImageUrl;
    private String creatorId;
    private String creatorNickname;
    private String creatorProfileImage;
    private int likeCount;
    private int downloadCount;
    private String createdAt;
    private boolean isLiked;    //좋아요 여부

    public FontWithUserProfileResponse(
            Integer fontId,
            String fontName,
            String otfUrl,
            String ttfUrl,
            String description,
            String originalImageUrl,
            String creatorId,
            String creatorNickname,
            String rawProfileImage,  // db에서 반환받은 값
            int likeCount,
            int downloadCount,
            String createdAt,
            boolean isLiked     //좋아요 여부
    ) {
        this.fontId = fontId;
        this.fontName = fontName;
        this.otfUrl = otfUrl;
        this.ttfUrl = ttfUrl;
        this.description = description;
        this.originalImageUrl = originalImageUrl;
        this.creatorId = creatorId;
        this.creatorNickname = creatorNickname;
        this.creatorProfileImage = rawProfileImage != null && !rawProfileImage.startsWith("/profiles/") //경로 지정. Webconfig 참고
                ? "/profiles/" + rawProfileImage
                : rawProfileImage;
        this.likeCount = likeCount;
        this.downloadCount = downloadCount;
        this.createdAt = createdAt;
        this.isLiked = isLiked; //좋아요 여부
    }
}
