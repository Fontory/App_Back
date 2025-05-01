package com.fontservice.fontory.service;

import com.fontservice.fontory.dto.mypage.FontResponseDto;
import com.fontservice.fontory.dto.sheet.PracticeSheetResponse;
import com.fontservice.fontory.repository.FontLikeRepository;
import com.fontservice.fontory.repository.FontRepository;
import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.repository.PracticeSheetRepository;
import com.fontservice.fontory.repository.SavedFontRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final FontRepository fontRepository;
    private final SavedFontRepository savedFontRepository;
    private final FontLikeRepository fontLikeRepository;
    private final PracticeSheetRepository practiceSheetRepository;

    //사용자가 만든 폰트 조회
    public List<FontResponseDto> getMyFonts(User user) {
        return fontRepository.findByUserId(user.getUserId())
                .stream()
                .map(FontResponseDto::new)
                .collect(Collectors.toList());
    }

    //사용자가 다운로드한 폰트 조회
    public List<FontResponseDto> getDownloadedFonts(User user) {
        return savedFontRepository.findSavedFontsByUserId(user.getUserId())
                .stream()
                .map(FontResponseDto::new)
                .collect(Collectors.toList());
    }

    //사용자가 좋아요한 폰트 조회
    public List<FontResponseDto> getLikedFonts(User user) {
        return fontLikeRepository.findLikedFontsByUserId(user.getUserId())
                .stream()
                .map(FontResponseDto::new)
                .collect(Collectors.toList());
    }

    //사용자가 만든 연습장 목록 조회
    public List<PracticeSheetResponse> getPracticeSheets(User user) {
        return practiceSheetRepository.findByUserId(user.getUserId())
                .stream()
                .map(PracticeSheetResponse::new)
                .collect(Collectors.toList());
    }


}
