package com.fontservice.fontory.service;

import com.fontservice.fontory.domain.Font;
import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.font.FontCreateRequest;
import com.fontservice.fontory.repository.FontRepository;
import com.fontservice.fontory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FontService {

    private final FontRepository fontRepository;
    private final UserRepository userRepository;

    public int createFont(String userId, String fontName, String otfUrl, String ttfUrl, String originalImageUrl, float vectorSimilarity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Font font = Font.builder()
                .user(user)
                .name(fontName)
                .otfUrl(otfUrl)
                .ttfUrl(ttfUrl)
                .description(null)  // 필요시 추후 확장
                .originalImageUrl(originalImageUrl)
                .vectorSimilarity(vectorSimilarity)
                .isPublic(Font.PublicStatus.N)  // 기본 비공개
                .likeCount(0)
                .downloadCount(0)
                .build();

        fontRepository.save(font);
        return font.getFontId();
    }
}

