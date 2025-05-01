package com.fontservice.fontory.dto.font;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FontDetailResponse {

    private Integer fontId;
    private String name;
    private String userId;
    private Integer likeCount;
    private Integer downloadCount;
    private String originalImageUrl;
}
