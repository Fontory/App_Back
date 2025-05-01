package com.fontservice.fontory.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostListWrapperResponseDto {
    private int totalCount;
    private List<PostListResponseDto> posts;
}