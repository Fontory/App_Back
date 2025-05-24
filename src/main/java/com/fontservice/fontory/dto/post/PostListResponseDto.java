package com.fontservice.fontory.dto.post;

import com.fontservice.fontory.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostListResponseDto {
    private Long postId;
    private String imageUrl;
    private String content;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
    private int likeCount;

    public static PostListResponseDto fromEntity(Post post) {
        return new PostListResponseDto(
                post.getPostId(),
                post.getImageUrl(),
                post.getContent(),
                post.getUser().getNickname(),
                post.getUser().getProfileImage(),
                post.getCreatedAt(),
                post.getLikeCount()
        );
    }
}