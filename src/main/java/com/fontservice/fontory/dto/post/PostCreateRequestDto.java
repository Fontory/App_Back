package com.fontservice.fontory.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequestDto {

    @Schema(description = "게시글 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "게시글 내용", example = "필사 명언 123")
    private String content;

    @Schema(description = "게시글 유형", example = "TRANSCRIPTION") // 또는 "GENERAL"
    private String postType;

    @Schema(description = "사용된 폰트 ID", example = "1")
    private Integer fontId;
}