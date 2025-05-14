package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.user.*;
import com.fontservice.fontory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "userId와 password로 로그인합니다.")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto requestDto,
            HttpSession session) {
        try {
            User user = userService.login(requestDto, session);

            LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                    user.getUserId(),
                    user.getNickname()
            );

            return ResponseEntity.ok(new LoginResponseDto(200, null, userInfo));

        } catch (RuntimeException e) {
            return ResponseEntity.ok(new LoginResponseDto(500, e.getMessage(), null));
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자 정보를 기반으로 회원가입합니다.")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequestDto requestDto,
                                                      HttpSession session) {
        String profileImageUrl = (String) session.getAttribute("profileImageUrl");
        requestDto.setProfileImage(profileImageUrl);

        return userService.signup(requestDto);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 세션을 만료시켜 로그아웃합니다.")
    public ResponseEntity<LogoutResponseDto> logout(HttpSession session) {
        LogoutResponseDto result = userService.logout(session);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/findId")
    @Operation(summary = "아이디 찾기", description = "이메일로 사용자 아이디를 찾습니다.")
    public ResponseEntity<FindUserIdResponseDto> findUserId(@RequestBody FindUserIdRequestDto requestDto) {
        FindUserIdResponseDto result = userService.findUserId(requestDto);
        return ResponseEntity.ok(result);
    }


    @PutMapping("/findPassword")
    @Operation(summary = "비밀번호 재설정", description = "ID와 Email 일치시 새 비밀번호를 입력하여 변경합니다.")
    public ResponseEntity<PasswordChangeResponseDto> resetPassword(@RequestBody PasswordChangeRequestDto request) {
        boolean result = userService.resetPassword(request);

        if (result) {
            return ResponseEntity.ok(
                    new PasswordChangeResponseDto(200, "비밀번호가 성공적으로 변경되었습니다.")
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new PasswordChangeResponseDto(400, "아이디와 이메일이 일치하지 않습니다.")
            );
        }
    }

    @PostMapping("/profile-image/signup")
    @Operation(summary = "회원가입 시 프로필 이미지 업로드", description = "사용자가 이미지 파일을 업로드하여 회원가입 합니다.")
    public ResponseEntity<?> uploadProfileImageForSignup(@RequestParam("image") MultipartFile file, HttpSession session) {
        try {
            String imageUrl = userService.storeProfileImageForSignup(file, session);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "회원가입용 프로필 이미지 업로드 성공",
                    "profileImageUrl", imageUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "message", "회원가입 이미지 업로드 실패: " + e.getMessage()
                    ));
        }
    }
}


