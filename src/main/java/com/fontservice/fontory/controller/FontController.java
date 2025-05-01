package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.Font;
import com.fontservice.fontory.domain.FontLike;
import com.fontservice.fontory.domain.SavedFont;
import com.fontservice.fontory.dto.font.FontDetailResponse;
import com.fontservice.fontory.dto.font.MyFontResponse;
import com.fontservice.fontory.repository.FontLikeRepository;
import com.fontservice.fontory.repository.FontRepository;
import com.fontservice.fontory.repository.SavedFontRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//파일 다운로드
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import java.io.File;



@RestController
@RequestMapping("/fonts")
@RequiredArgsConstructor
public class FontController {

    private final FontRepository fontRepository;
    private final SavedFontRepository savedFontRepository;
    private final FontLikeRepository fontLikeRepository;

    //모든 사용자의 공개폰트 인기순/최신순 정렬
    @GetMapping
    public List<Font> getFonts(@RequestParam(name = "sort", required = false, defaultValue = "latest") String sort) {
        if ("popular".equalsIgnoreCase(sort)) {
            return fontRepository.findByIsPublicOrderByLikeCountDesc(Font.PublicStatus.Y);
        } else {
            return fontRepository.findByIsPublicOrderByCreatedAtDesc(Font.PublicStatus.Y);
        }
    }

    //로그인된 사용자의 비공개 폰트리스트 로드
    @GetMapping("/private")
    public List<Font> getMyPrivateFonts(@RequestParam("userId") String userId) {
        return fontRepository.findByUserIdAndIsPublic(userId, Font.PublicStatus.N);
    }

    //공개로 전환
    @PutMapping("/{fontId}/publish")
    public String publishFont(
            @PathVariable("fontId") Integer fontId,
            @RequestParam("userId") String userId,
            @RequestParam("description") String description
    ) {
        Font font = fontRepository.findById(fontId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다: " + fontId));

        // 작성자 본인 확인
        if (!font.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 생성한 폰트만 공개할 수 있습니다.");
        }

        // 설명 수정 + 공개 전환
        font.setDescription(description);
        font.setIsPublic(Font.PublicStatus.Y);
        fontRepository.save(font);

        return "폰트 설명 수정 및 공개 완료";
    }

    //폰트 상세페이지 불러오기
    @GetMapping("/{fontId}")
    public FontDetailResponse getFontDetail(@PathVariable("fontId") Integer fontId) {
        Font font = fontRepository.findById(fontId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다: " + fontId));

        return FontDetailResponse.builder()
                .fontId(font.getFontId())
                .name(font.getName())
                .userId(font.getUserId())
                .likeCount(font.getLikeCount())
                .downloadCount(font.getDownloadCount())
                .originalImageUrl("/images/" + font.getOriginalImageUrl())
                .build();
    }

    //폰트 저장
    @PostMapping("/{fontId}/save")
    public String saveFont(
            @PathVariable("fontId") Integer fontId,
            @RequestParam("userId") String userId
    ) {
        // 이미 저장했는지 확인
        if (savedFontRepository.existsByUserIdAndFontId(userId, fontId)) {
            return "이미 저장한 폰트입니다.";
        }

        SavedFont savedFont = SavedFont.builder()
                .userId(userId)
                .fontId(fontId)
                .build();

        savedFontRepository.save(savedFont);

        return "폰트 저장 완료";
    }

    //TTF 파일 다운로드
    @GetMapping("/{fontId}/download/ttf")
    public ResponseEntity<Resource> downloadTtf(@PathVariable Integer fontId) {
        return downloadFontFile(fontId, "ttf");
    }

    //OTF 파일 다운로드
    @GetMapping("/{fontId}/download/otf")
    public ResponseEntity<Resource> downloadOtf(@PathVariable Integer fontId) {
        return downloadFontFile(fontId, "otf");
    }

    private ResponseEntity<Resource> downloadFontFile(Integer fontId, String format) {
        Font font = fontRepository.findById(fontId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다."));

        String filePath;
        String originalFileName;

        if (format.equals("ttf")) {
            filePath = font.getTtfUrl();
            originalFileName = font.getName() + ".ttf";
        } else if (format.equals("otf")) {
            filePath = font.getOtfUrl();
            originalFileName = font.getName() + ".otf";
        } else {
            throw new IllegalArgumentException("지원하지 않는 포맷입니다: " + format);
        }

        File file = new File("./uploads/fonts" + filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + filePath);
        }

        // 다운로드 수 증가
        font.setDownloadCount(font.getDownloadCount() + 1);
        fontRepository.save(font);

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename(originalFileName).build());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    //폰트 좋아요
    @PostMapping("/{fontId}/like")
    public String likeFont(
            @PathVariable("fontId") Integer fontId,
            @RequestParam("userId") String userId
    ) {
        if (fontLikeRepository.existsByUserIdAndFontId(userId, fontId)) {
            return "이미 좋아요를 누른 폰트입니다.";
        }

        Font font = fontRepository.findById(fontId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다."));

        FontLike fontLike = FontLike.builder()
                .userId(userId)
                .fontId(fontId)
                .build();

        fontLikeRepository.save(fontLike);

        // 좋아요 수 증가
        font.setLikeCount(font.getLikeCount() + 1);
        fontRepository.save(font);

        return "폰트 좋아요 완료";
    }

    //폰트선택 콤보박스용 내가 만든 공개/비공개 폰트 전부 +  저장한 폰트
    @GetMapping("/my")
    public List<MyFontResponse> getMyFonts(@RequestParam("userId") String userId) {
        List<MyFontResponse> result = new ArrayList<>();

        // 내가 만든 폰트
        List<Font> createdFonts = fontRepository.findByUserId(userId);
        for (Font font : createdFonts) {
            result.add(MyFontResponse.builder()
                    .fontId(font.getFontId())
                    .name(font.getName())
                    .type("created")
                    .build());
        }

        // 내가 저장한 폰트
        List<Font> savedFonts = savedFontRepository.findSavedFontsByUserId(userId);
        for (Font font : savedFonts) {
            result.add(MyFontResponse.builder()
                    .fontId(font.getFontId())
                    .name(font.getName())
                    .type("saved")
                    .build());
        }

        return result;
    }


}
