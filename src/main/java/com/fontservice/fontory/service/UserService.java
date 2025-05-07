package com.fontservice.fontory.service;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.enums.UserRole;
import com.fontservice.fontory.domain.enums.UserStatus;
import com.fontservice.fontory.dto.mypage.ProfileResponseDto;
import com.fontservice.fontory.dto.mypage.ProfileUpdateRequestDto;
import com.fontservice.fontory.dto.user.*;
import com.fontservice.fontory.repository.UserRepository;
import com.fontservice.fontory.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    //로그인 기능
    public User login(LoginRequestDto request, HttpSession session) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        session.setAttribute("user", user); // 로그인 세션 저장
        return user;
    }

    //회원가입 기능
        public SignupResponseDto signup(SignupRequestDto dto) {
            // 아이디 중복 확인
            if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
                return new SignupResponseDto(409, "이미 존재하는 ID입니다.", null);
            }

            // 이메일 중복 확인
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                return new SignupResponseDto(409, "이미 존재하는 이메일입니다.", null);
            }

            // 비밀번호 불일치
            if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
                return new SignupResponseDto(400, "비밀번호가 일치하지 않습니다.", null);
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

            // JWT 토큰 발급
            String accessToken = jwtUtil.createToken(user.getUserId());

            return new SignupResponseDto(200, "회원가입이 완료되었습니다.", accessToken);
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
    public PasswordChangeResponseDto changePassword(
            PasswordChangeRequestDto dto, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new PasswordChangeResponseDto(500, "로그인된 사용자가 아닙니다.");
        }

        if (!user.getPassword().equals(dto.getCurrentPassword())) {
            return new PasswordChangeResponseDto(500, "현재 비밀번호가 일치하지 않습니다.");
        }

        if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            return new PasswordChangeResponseDto(500, "새 비밀번호가 서로 다릅니다.");
        }

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);

        return new PasswordChangeResponseDto(200, "비밀번호가 성공적으로 변경되었습니다.");
    }

    public User getSessionUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    //mypage - 프로필 조회
    public ProfileResponseDto getProfile(User user) {
        return new ProfileResponseDto(
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage()
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
        if (dto.getProfileImage() != null) {
            user.setProfileImage(dto.getProfileImage());
        }
        userRepository.save(user);
    }
}
