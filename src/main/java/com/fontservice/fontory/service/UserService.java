package com.fontservice.fontory.service;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.enums.UserRole;
import com.fontservice.fontory.domain.enums.UserStatus;
import com.fontservice.fontory.dto.mypage.ProfileResponseDto;
import com.fontservice.fontory.dto.mypage.ProfileUpdateRequestDto;
import com.fontservice.fontory.dto.user.*;
import com.fontservice.fontory.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //로그인 기능
    public User login(LoginRequestDto request, HttpSession session) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("user", user); // 로그인 세션 저장

        return user;
    }

    public ResponseEntity<Map<String, Object>> signup(SignupRequestDto dto) {
        // 아이디 중복 확인
        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            return ResponseEntity.status(409).body(
                    Map.of("status", 409, "message", "이미 존재하는 ID입니다.")
            );
        }

        // 이메일 중복 확인
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(
                    Map.of("status", 409, "message", "이미 존재하는 이메일입니다.")
            );
        }

        // 비밀번호 불일치
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            return ResponseEntity.status(400).body(
                    Map.of("status", 400, "message", "비밀번호가 일치하지 않습니다.")
            );
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .userId(dto.getUserId())
                .password(encodedPassword)
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .profileImage(dto.getProfileImage())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("status", 200, "message", "회원가입이 완료되었습니다."));
    }

    //로그아웃 기능
    public LogoutResponseDto logout(HttpSession session) {
        session.invalidate();  // 세션 완전 제거 (로그아웃)
        return new LogoutResponseDto(200, "로그아웃 되었습니다.");
    }

    //아이디 찾기
    public FindUserIdResponseDto findUserId(FindUserIdRequestDto dto) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());

        if (user.isEmpty()) {
            return new FindUserIdResponseDto(500, "일치하는 이메일이 없습니다.", null);
        }

        return new FindUserIdResponseDto(200, "아이디를 찾았습니다.", user.get().getUserId());
    }

    //비밀번호 찾기
    public boolean resetPassword(PasswordChangeRequestDto request) {
        Optional<User> optionalUser = userRepository.findByUserIdAndEmail(request.getUserId(), request.getEmail());

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return true;
    }

    public User getSessionUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }



    //mypage - 프로필 조회
    public ProfileResponseDto getProfile(User user) {
        return new ProfileResponseDto(
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage(),
                user.getName(),
                user.getPhone()
        );
    }

    //mypage - 프로필 수정
    public void updateProfile(User user, ProfileUpdateRequestDto dto) {
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getName() != null) {
        user.setName(dto.getName());
    }
        if (dto.getPhone() != null) {
        user.setPhone(dto.getPhone());
    }
        userRepository.save(user);
}

    // 회원가입용 이미지 업로드 로직
    public String storeProfileImageForSignup(MultipartFile file, HttpSession session) throws IOException {

        //저장 디렉토리 설정
        String uploadDir = "/home/t25123/v0.5src/mobile/App_Back/uploads/profile";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        //파일 이름 생성 (UUID 기반)
        String fileName = UUID.randomUUID().toString() + ".jpg";
        String savePath = uploadDir + File.separator + fileName;

        //저장
        file.transferTo(new File(savePath));

        //URL 생성
        String imageUrl = "/uploads/profile/" + fileName;

        //세션 갱신
        session.setAttribute("profileImageUrl", imageUrl);

        return imageUrl;
    }

    // 마이페이지용 이미지 삭제 및 재업로드 로직
    public String storeProfileImage(MultipartFile file, HttpSession session) throws IOException {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("세션에 userId가 없습니다.");
        }

        // 저장 디렉토리
        String uploadDir = "/home/t25123/v0.5src/mobile/App_Back/uploads/profile";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // 기존 이미지 삭제
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음"));

        String oldImageUrl = user.getProfileImage();
        if (oldImageUrl != null && oldImageUrl.startsWith("/uploads/profile/")) {
            String oldFilePath = uploadDir + File.separator + oldImageUrl.substring("/uploads/profile/".length());
            File oldFile = new File(oldFilePath);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        // 새 파일 이름 생성 및 저장
        String fileName = UUID.randomUUID().toString() + ".jpg";
        String savePath = uploadDir + File.separator + fileName;
        file.transferTo(new File(savePath));

        // 새 이미지 URL 생성
        String imageUrl = "/uploads/profile/" + fileName;
        user.setProfileImage(imageUrl);
        userRepository.save(user);

        // 세션 갱신
        session.setAttribute("user", user);

        return imageUrl;
    }
}
