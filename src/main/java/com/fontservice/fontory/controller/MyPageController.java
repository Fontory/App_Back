package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.common.SimpleResponseDto;
import com.fontservice.fontory.dto.mypage.BadgeResponseDto;
import com.fontservice.fontory.dto.mypage.FontResponseDto;
import com.fontservice.fontory.dto.mypage.ProfileResponseDto;
import com.fontservice.fontory.dto.mypage.ProfileUpdateRequestDto;
import com.fontservice.fontory.dto.post.PostListResponseDto;
import com.fontservice.fontory.dto.sheet.PracticeSheetResponse;
import com.fontservice.fontory.service.BadgeService;
import com.fontservice.fontory.service.MypageService;
import com.fontservice.fontory.service.PostService;
import com.fontservice.fontory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final PostService postService;
    private final UserService userService;
    private final BadgeService badgeService;
    private final MypageService mypageService;

    @GetMapping("/posts")
    @Operation(summary = "사용자가 작성한 필사 게시물 조회")
    public SimpleResponseDto<List<PostListResponseDto>> getMyPosts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(500, "로그인이 필요합니다.", null);
        }

        List<PostListResponseDto> myPosts = postService.getMyPosts(user);
        return new SimpleResponseDto<>(200, "내 필사 게시물 조회 성공", myPosts);
    }

    @GetMapping("/postLikes")
    @Operation(summary = "사용자가 좋아요 누른 필사 게시물 조회")
    public SimpleResponseDto<List<PostListResponseDto>> getLikedPosts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(500, "로그인이 필요합니다.", null);
        }

        List<PostListResponseDto> likedPosts = postService.getLikedPosts(user);
        return new SimpleResponseDto<>(200, "좋아요한 필사 게시물 조회 성공", likedPosts);
    }

    @GetMapping("/profile")
    @Operation(summary = "사용자 프로필 조회")
    public SimpleResponseDto<ProfileResponseDto> getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(500, "로그인이 필요합니다.", null);
        }

        ProfileResponseDto profile = userService.getProfile(user);
        return new SimpleResponseDto<>(200, "프로필 조회 성공", profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "사용자 프로필 수정", description = "사용자가 수정하길 원하는 텍스트 정보를 수정 가능합니다.")
    public SimpleResponseDto<?> updateProfile(@RequestBody ProfileUpdateRequestDto dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(500, "로그인이 필요합니다.", null);
        }

        userService.updateProfile(user, dto);
        return new SimpleResponseDto<>(200, "프로필 수정 성공", null);
    }

    @PostMapping("/profile-image")
    @Operation(summary = "사용자 프로필 이미지 수정", description = "사용자가 프로필 이미지를 업로드해서 수정합니다.")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile file,
                                                HttpSession session) {
        try {
            String imageUrl = userService.storeProfileImage(file, session);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "프로필 이미지 수정 성공",
                    "profileImageUrl", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "message", "이미지 업로드 실패: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/badges")
    @Operation(summary = "뱃지 전체 목록 조회")
    public SimpleResponseDto<List<BadgeResponseDto>> getAllBadges() {
        List<BadgeResponseDto> badges = badgeService.getAllBadges();
        return new SimpleResponseDto<>(200, "전체 뱃지 조회 성공", badges);
    }

    @GetMapping("/badges/my")
    @Operation(summary = "사용자가 획득한 모든 뱃지 조회 가능", description = "사용자의 필사 게시물 작성 횟수에 따라 자동 반영 됩니다.")
    public SimpleResponseDto<List<BadgeResponseDto>> getMyBadges(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(401, "로그인이 필요합니다.", null);
        }

        List<BadgeResponseDto> myBadges = badgeService.getMyBadges(user);
        return new SimpleResponseDto<>(200, "나의 뱃지 조회 성공", myBadges);
    }

    @GetMapping("/fonts/my")
    @Operation(summary = "사용자가 만든 폰트 조회")
    public SimpleResponseDto<List<FontResponseDto>> getMyFonts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(401, "로그인이 필요합니다.", null);
        }

        List<FontResponseDto> fonts = mypageService.getMyFonts(user);
        return new SimpleResponseDto<>(200, "내가 만든 폰트 조회 성공", fonts);
    }

    @GetMapping("/fonts/downloads")
    @Operation(summary = "사용자가 다운로드한 폰트 조회")
    public SimpleResponseDto<List<FontResponseDto>> getDownloadedFonts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(401, "로그인이 필요합니다.", null);
        }

        List<FontResponseDto> fonts = mypageService.getDownloadedFonts(user);
        return new SimpleResponseDto<>(200, "다운로드한 폰트 조회 성공", fonts);
    }

    @GetMapping("/fonts/likes")
    @Operation(summary = "사용자가 좋아요한 폰트 조회")
    public SimpleResponseDto<List<FontResponseDto>> getLikedFonts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(401, "로그인이 필요합니다.", null);
        }

        List<FontResponseDto> fonts = mypageService.getLikedFonts(user);
        return new SimpleResponseDto<>(200, "좋아요한 폰트 조회 성공", fonts);
    }

    @GetMapping("/practices")
    @Operation(summary = "사용자가 만든 연습장 조회")
    public SimpleResponseDto<List<PracticeSheetResponse>> getPracticeSheets(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new SimpleResponseDto<>(401, "로그인이 필요합니다.", null);
        }

        List<PracticeSheetResponse> sheets = mypageService.getPracticeSheets(user);
        return new SimpleResponseDto<>(200, "내 연습장 조회 성공", sheets);
    }
}