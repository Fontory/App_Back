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
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final BadgeService badgeService;

    // 게시물 글 작성
    public SimpleResponseDto createPost(PostCreateRequestDto dto, MultipartFile imageFile, HttpSession session) {
        System.out.println("[게시물 등록] 요청 시작");

        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("[게시물 등록] 로그인되지 않은 사용자 요청");
            return new SimpleResponseDto(500, "로그인이 필요합니다.", null);
        }
        System.out.println("[게시물 등록] 사용자 인증 성공: " + user.getUserId());

        PostType type;
        try {
            type = PostType.valueOf(dto.getPostType());
            System.out.println("[게시물 등록] 게시물 타입 확인 성공: " + type);
        } catch (IllegalArgumentException e) {
            System.out.println("[게시물 등록] 유효하지 않은 게시물 타입: " + dto.getPostType());
            return new SimpleResponseDto(500, "유효하지 않은 게시글 유형입니다.", null);
        }

        // 이미지 파일 업로드 처리
        String uploadDir = "/home/t25123/v0.5src/mobile/App_Back/uploads/post";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("[게시물 등록] 업로드 디렉토리 생성: " + uploadDir);
        }

        String fileName = UUID.randomUUID().toString() + ".jpg";
        String savePath = uploadDir + File.separator + fileName;

        try {
            imageFile.transferTo(new File(savePath));
            System.out.println("[게시물 등록] 이미지 업로드 성공: " + savePath);
        } catch (IOException e) {
            System.out.println("[게시물 등록] 이미지 업로드 실패: " + e.getMessage());
            return new SimpleResponseDto(500, "이미지 업로드 실패: " + e.getMessage(), null);
        }

        String imageUrl = "/uploads/post/" + fileName;

        Post post = Post.builder()
                .user(user)
                .imageUrl(imageUrl)
                .content(dto.getContent())
                .postType(type)
                .fontId(dto.getFontId())
                .likeCount(0)
                .build();

        postRepository.save(post);
        System.out.println("[게시물 등록] 게시물 DB 저장 완료: post_id=" + post.getPostId());

        badgeService.checkAndAcquireBadge(user);
        System.out.println("[게시물 등록] 뱃지 체크 완료");

        System.out.println("[게시물 등록] 요청 완료");
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
                        .profileImage(post.getUser().getProfileImage())
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
                .profileImage(post.getUser().getProfileImage())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikeCount())
                .fontId(post.getFontId())
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
                            post.getUser().getProfileImage(),
                            post.getCreatedAt(),
                            post.getLikeCount()
                    );
                })
                .collect(Collectors.toList());
    }
}