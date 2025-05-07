package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.user.*;
import com.fontservice.fontory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        SignupResponseDto result = userService.signup(requestDto);
        return ResponseEntity.ok(result);
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
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호와 새 비밀번호를 입력하여 비밀번호를 변경합니다.")
    public ResponseEntity<PasswordChangeResponseDto> changePassword(
            @RequestBody PasswordChangeRequestDto requestDto,
            HttpSession session) {

        PasswordChangeResponseDto result = userService.changePassword(requestDto, session);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/uploadProfile")
    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 업로드하고, 접근 가능한 URL을 반환합니다.")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. 파일명 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 2. 저장 디렉터리 & 경로 설정
        String uploadDir = "/home/t25123/v0.5src/mobile/App_Back/uploads/profile/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("📁 디렉터리 생성됨: " + uploadDir);
        }

        // 3. 저장할 파일 경로
        String filePath = uploadDir + fileName;
        System.out.println("📝 저장할 파일 경로: " + filePath);

        // 4. 실제 파일 저장
        file.transferTo(new File(filePath));
        System.out.println("✅ 파일 저장 완료: " + fileName);

        // 5. 접근 가능한 URL 생성
        String profileImageUrl = "http://ceprj.gachon.ac.kr:60023/profile/" + fileName;
        System.out.println("🌐 프론트에 전달할 URL: " + profileImageUrl);

        return ResponseEntity.ok(profileImageUrl);
    }
}


