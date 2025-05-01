package com.fontservice.fontory.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostDetailResponseDto {
    private Long postId;
    private String imageUrl;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private int likeCount;
}