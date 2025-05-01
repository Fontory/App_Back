package com.fontservice.fontory.service;

import com.fontservice.fontory.domain.Post;
import com.fontservice.fontory.domain.PostLike;
import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.enums.PostType;
import com.fontservice.fontory.dto.post.PostCreateRequestDto;
import com.fontservice.fontory.dto.common.SimpleResponseDto;
import com.fontservice.fontory.dto.post.PostDetailResponseDto;
import com.fontservice.fontory.dto.post.PostListResponseDto;
import com.fontservice.fontory.dto.post.PostListWrapperResponseDto;
import com.fontservice.fontory.repository.PostLikeRepository;
import com.fontservice.fontory.repository.PostRepository;
import com.fontservice.fontory.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;

    //게시물 글 작성
    public SimpleResponseDto createPost(PostCreateRequestDto dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto(500, "로그인이 필요합니다.", null);
        }

        PostType type;
        try {
            type = PostType.valueOf(dto.getPostType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new SimpleResponseDto(500, "유효하지 않은 게시글 유형입니다.", null);
        }

        Post post = Post.builder()
                .user(user)
                .imageUrl(dto.getImageUrl())
                .content(dto.getContent())
                .postType(type)
                .fontId(dto.getFontId())
                .likeCount(0)
                .build();

        postRepository.save(post);

        badgeService.checkAndAcquireBadge(user);

        return new SimpleResponseDto(200, "게시물이 등록되었습니다.", null);
    }

    //게시물 목록 조회
    public PostListWrapperResponseDto getPostList(String sort) {
        List<Post> posts;

        if ("popular".equalsIgnoreCase(sort)) {
            posts = postRepository.findAllByOrderByLikeCountDesc();
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        List<PostListResponseDto> postDtos = posts.stream()
                .map(post -> PostListResponseDto.builder()
                        .postId(post.getPostId())
                        .imageUrl(post.getImageUrl())
                        .content(post.getContent())
                        .nickname(post.getUser().getNickname())
                        .createdAt(post.getCreatedAt())
                        .likeCount(post.getLikeCount())
                        .build())
                .collect(Collectors.toList());

        return PostListWrapperResponseDto.builder()
                .totalCount(postDtos.size())
                .posts(postDtos)
                .build();
    }

    //게시물 글 상세 조회
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        return PostDetailResponseDto.builder()
                .postId(post.getPostId())
                .imageUrl(post.getImageUrl())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .build();
    }

    //게시물 글 토글 메서드
    @Transactional
    public String toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<PostLike> like = postLikeRepository.findByPostAndUser(post, user);

        if (like.isPresent()) {
            // 좋아요 취소
            postLikeRepository.delete(like.get());
            post.setLikeCount(post.getLikeCount() - 1);
            return "좋아요 취소";
        } else {
            // 좋아요 추가
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .likedAt(LocalDateTime.now())
                    .build();
            postLikeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            return "좋아요 완료";
        }
    }

    //마이페이지 - 작성한 필사 게시글 조회
    public List<PostListResponseDto> getMyPosts(User user) {
        List<Post> posts = postRepository.findByUser(user);

        return posts.stream()
                .map(PostListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    //마이페이지 - 좋아요한 필사 게시글 조회
    public List<PostListResponseDto> getLikedPosts(User user) {
        List<PostLike> likes = postLikeRepository.findByUser(user);

        return likes.stream()
                .map(like -> {
                    Post post = like.getPost();
                    return new PostListResponseDto(
                            post.getPostId(),
                            post.getImageUrl(),
                            post.getContent(),
                            post.getUser().getNickname(),
                            post.getCreatedAt(),
                            post.getLikeCount()
                    );
                })
                .collect(Collectors.toList());
    }
}