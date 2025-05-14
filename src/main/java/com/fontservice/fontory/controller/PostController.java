package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.post.PostCreateRequestDto;
import com.fontservice.fontory.dto.common.SimpleResponseDto;
import com.fontservice.fontory.dto.post.PostDetailResponseDto;
import com.fontservice.fontory.dto.post.PostListWrapperResponseDto;
import com.fontservice.fontory.service.PostService;
import com.fontservice.fontory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "게시물 작성", description = "로그인한 사용자가 필사 or 일반 게시물을 작성합니다.")
    public ResponseEntity<SimpleResponseDto> createPost(
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("content") String content,
            @RequestParam("postType") String postType,
            @RequestParam("fontId") Integer fontId,
            HttpSession session) {

        PostCreateRequestDto dto = new PostCreateRequestDto(content, postType, fontId);
        return ResponseEntity.ok(postService.createPost(dto, imageFile, session));
    }

    @GetMapping
    @Operation(summary = "게시물 목록 조회", description = "로그인한 사용자가 최신순/인기순으로 게시물을 조회합니다.(기본=최신순)")
    public SimpleResponseDto<PostListWrapperResponseDto> getPosts(@RequestParam(defaultValue = "latest") String sort) {
        PostListWrapperResponseDto responseData = postService.getPostList(sort);
        return SimpleResponseDto.success("게시글 목록 조회 성공", responseData);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시물 상세 조회", description = "게시물을 클릭했을때 나오는 게시물을 상세 조회 가능합니다.")
    public SimpleResponseDto<PostDetailResponseDto> getPostDetail(@PathVariable Long postId) {
        PostDetailResponseDto responseData = postService.getPostDetail(postId);
        return SimpleResponseDto.success("게시글 상세 조회 성공", responseData);
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "게시물 좋아요/좋아요 취소", description = "사용자가 게시물에 좋아요/좋아요 취소가 가능합니다.")
    public SimpleResponseDto<Void> toggleLike(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        User user = userService.getSessionUser(request);
        String result = postService.toggleLike(postId, user);
        return new SimpleResponseDto<>(200, result, null);
    }

}

