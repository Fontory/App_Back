package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.Font;
import com.fontservice.fontory.domain.FontLike;
import com.fontservice.fontory.domain.SavedFont;
import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.font.FontDetailResponse;
import com.fontservice.fontory.dto.font.FontWithUserProfileResponse;
import com.fontservice.fontory.dto.font.MyFontResponse;
import com.fontservice.fontory.repository.FontLikeRepository;
import com.fontservice.fontory.repository.FontRepository;
import com.fontservice.fontory.repository.SavedFontRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

//파일 다운로드
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;

// 이미지 렌더링 관련
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;

// 파일 스트림 및 변환 관련
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

@RestController
@RequestMapping("/fonts")
@RequiredArgsConstructor
public class FontController {

    private final FontRepository fontRepository;
    private final SavedFontRepository savedFontRepository;
    private final FontLikeRepository fontLikeRepository;

    //모든 사용자의 공개폰트 인기순/최신순 정렬
    @GetMapping
    public List<FontWithUserProfileResponse> getFonts(
            @RequestParam(name = "sort", required = false, defaultValue = "latest") String sort,
            HttpServletRequest request  // 세션에서 사용자 확인
    ) {
        // 로그인 유저 가져오기
        User sessionUser = (User) request.getSession().getAttribute("user");
        String userId = sessionUser != null ? sessionUser.getUserId() : null;

        // 정렬조건에 따라 폰트 조회
        List<Font> fonts;
        if ("popular".equalsIgnoreCase(sort)) {
            fonts = fontRepository.findWithUserByIsPublicOrderByLikeCountDesc(Font.PublicStatus.Y);
        } else {
            fonts = fontRepository.findWithUserByIsPublicOrderByCreatedAtDesc(Font.PublicStatus.Y);
        }

        // 응답 DTO리스트 구성
        List<FontWithUserProfileResponse> responseList = new ArrayList<>();
        for (Font font : fonts) {
            var user = font.getUser();

            // 좋아요 여부 판단
            boolean isLiked = userId != null && fontLikeRepository.existsByUserIdAndFontId(userId, font.getFontId());

            responseList.add(new FontWithUserProfileResponse(
                    font.getFontId(),
                    font.getName(),
                    font.getOtfUrl(),
                    font.getTtfUrl(),
                    font.getDescription(),
                    font.getOriginalImageUrl(),
                    user.getUserId(),
                    user.getNickname(),
                    user.getProfileImage(),
                    font.getLikeCount(),
                    font.getDownloadCount(),
                    font.getCreatedAt().toString(),
                    isLiked  // 좋아요 여부 포함
            ));
        }

        return responseList;
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
    @GetMapping("/api/{fontId}")
    public FontDetailResponse getFontDetail(
            @PathVariable("fontId") Integer fontId,
            HttpServletRequest request
    ) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        String userId = sessionUser != null ? sessionUser.getUserId() : null;

        Font font = fontRepository.findWithUserByFontId(fontId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다."));

        boolean liked = false;
        if (userId != null) {
            liked = fontLikeRepository.existsByUserIdAndFontId(userId, fontId);
        }

        return FontDetailResponse.builder()
                .fontId(font.getFontId())
                .fontName(font.getName())
                .creatorId(font.getUser().getUserId())
                .creatorNickname(font.getUser().getNickname())
                .creatorProfileImage(font.getUser().getProfileImage())
                .likeCount(font.getLikeCount())
                .downloadCount(font.getDownloadCount())
                .ttfUrl(font.getTtfUrl())
                .otfUrl(font.getOtfUrl())
                .description(font.getDescription())
                .originalImageUrl(font.getOriginalImageUrl())
                .createdAt(font.getCreatedAt().toString())
                .liked(liked)
                .build();

    }




    //폰트 저장
    @PostMapping("/{fontId}/save")
    public String saveFont(
            @PathVariable("fontId") Integer fontId,
            @RequestParam("userId") String userId
    ) {
        System.out.println("📥 폰트 저장 요청: fontId=" + fontId + ", userId=" + userId);

        // 이미 저장했는지 확인
        if (savedFontRepository.existsByUserIdAndFontId(userId, fontId)) {
            return "이미 저장한 폰트입니다.";
        }

        SavedFont savedFont = SavedFont.builder()
                .userId(userId)
                .fontId(fontId)
                .savedAt(LocalDateTime.now())  // ✅ 이 줄을 반드시 추가
                .build();

        savedFontRepository.save(savedFont);

        return "폰트 저장 완료";
    }

    //TTF 파일 다운로드
    @GetMapping("/{fontId}/download/ttf")
    public ResponseEntity<Resource> downloadTtf(
            @PathVariable Integer fontId,
            @RequestParam("userId") String userId
    ) {
        return downloadFontFile(fontId, "ttf", userId);
    }


    @GetMapping("/{fontId}/download/otf")
    public ResponseEntity<Resource> downloadOtf(
            @PathVariable Integer fontId,
            @RequestParam("userId") String userId
    ) {
        return downloadFontFile(fontId, "otf", userId);
    }


    private ResponseEntity<Resource> downloadFontFile(Integer fontId, String format, String userId)
    {
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

        // 다운로드 수 증가(같은 유저 중복 다운로드시 다운로드 수 증가 없음)
        if (!savedFontRepository.existsByUserIdAndFontId(userId, fontId)) {
            font.setDownloadCount(font.getDownloadCount() + 1);
            fontRepository.save(font);

            SavedFont savedFont = SavedFont.builder()
                    .userId(userId)
                    .fontId(fontId)
                    .build();
            savedFontRepository.save(savedFont);
        }

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
                .likedAt(LocalDateTime.now()) 
                .build();

        fontLikeRepository.save(fontLike);

        // 좋아요 수 증가
        font.setLikeCount(font.getLikeCount() + 1);
        fontRepository.save(font);

        return "폰트 좋아요 완료";
    }

    //폰트 좋아요 취소
    @DeleteMapping("/{fontId}/like")
    public String unlikeFont(
            @PathVariable("fontId") Integer fontId,
            @RequestParam("userId") String userId
    ) {
        System.out.println("💔 좋아요 취소 요청: fontId=" + fontId + ", userId=" + userId);  // ✅ 추가

        FontLike fontLike = fontLikeRepository.findByUserIdAndFontId(userId, fontId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요한 기록이 없습니다."));

        fontLikeRepository.delete(fontLike);

        Font font = fontRepository.findById(fontId)
                .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다."));

        font.setLikeCount(Math.max(0, font.getLikeCount() - 1));
        fontRepository.save(font);

        return "좋아요 취소 완료";
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

    //폰트 설명글 이미지 렌더링
    @GetMapping("/{fontId}/render")
    public ResponseEntity<byte[]> renderFontDescription(
            @PathVariable("fontId") Integer fontId,
            @RequestParam(name = "text", required = false, defaultValue = "샘플 미리보기입니다.") String text
    ) {
        try {
            com.fontservice.fontory.domain.Font font = fontRepository.findById(fontId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 폰트를 찾을 수 없습니다."));

            String ttfUrl = font.getTtfUrl();
            if (!ttfUrl.startsWith("/")) {
                ttfUrl = "/" + ttfUrl;
            }
            String fontFilePath = "./uploads/fonts" + ttfUrl;

            float fontSize = 48f;
            java.awt.Font awtFont = java.awt.Font.createFont(
                    java.awt.Font.TRUETYPE_FONT,
                    new FileInputStream(fontFilePath)
            ).deriveFont(java.awt.Font.PLAIN, fontSize);

            int maxWidth = 700;
            int padding = 40;

            // 측정용 Graphics2D 생성
            BufferedImage tmpImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tmpG = tmpImage.createGraphics();
            tmpG.setFont(awtFont);
            FontMetrics metrics = tmpG.getFontMetrics();

            // 줄 단위로 나누기
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (char c : text.toCharArray()) {
                line.append(c);
                if (metrics.stringWidth(line.toString()) > maxWidth - padding * 2) {
                    line.deleteCharAt(line.length() - 1);
                    lines.add(line.toString());
                    line = new StringBuilder().append(c);
                }
            }
            if (!line.isEmpty()) lines.add(line.toString());

            tmpG.dispose();

            int lineHeight = metrics.getHeight();
            int imageHeight = lineHeight * lines.size() + padding * 2;

            // ✅ 최소 이미지 너비 보장 (500 이상)
            int textWidth = lines.stream()
                    .mapToInt(metrics::stringWidth)
                    .max()
                    .orElse(0);

            int imageWidth = Math.max(500, Math.min((int)(textWidth * 1.2) + padding * 2, 800));

            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(new Color(249, 249, 249)); // ✔️ 배경색 #f9f9f9
            g2d.fillRect(0, 0, imageWidth, imageHeight);

            g2d.setColor(Color.BLACK);
            g2d.setFont(awtFont);

            // 왼쪽 정렬
            FontRenderContext frc = g2d.getFontRenderContext();
            int y = padding;
            for (String l : lines) {
                TextLayout layout = new TextLayout(l, awtFont, frc);
                layout.draw(g2d, padding, y + metrics.getAscent());
                y += lineHeight;
            }

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("이미지 렌더링 실패: " + e.getMessage()).getBytes());
        }
    }
}


