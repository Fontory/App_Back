package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.Background;
import com.fontservice.fontory.domain.Font;
import com.fontservice.fontory.domain.PracticeSheet;
import com.fontservice.fontory.dto.sheet.PracticeSheetRequest;
import com.fontservice.fontory.dto.sheet.PracticeSheetResponse;
import com.fontservice.fontory.repository.BackgroundRepository;
import com.fontservice.fontory.repository.FontRepository;
import com.fontservice.fontory.repository.PracticeSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;



@RestController
@RequestMapping("/practice-sheets")
@RequiredArgsConstructor
public class PracticeSheetController {

    private final PracticeSheetRepository practiceSheetRepository;
    private final BackgroundRepository backgroundRepository;
    private final FontRepository fontRepository;

    //연습장 생성
    @PostMapping
    public PracticeSheetResponse createPracticeSheet(@RequestBody PracticeSheetRequest request) {
        try {
            // 1. 배경 이미지 로드
            Background background = backgroundRepository.findById(request.getBackgroundId())
                    .orElseThrow(() -> new IllegalArgumentException("배경을 찾을 수 없습니다."));
            File backgroundFile = new File("./uploads/backgrounds/" + background.getImageUrl());
            if (!backgroundFile.exists()) {
                throw new IOException("배경 이미지 파일이 없습니다: " + background.getImageUrl());
            }
            BufferedImage bgImage = ImageIO.read(backgroundFile);

            // 2. 사용자 폰트 로드
            Font fontMeta = fontRepository.findById(request.getFontId())
                    .orElseThrow(() -> new IllegalArgumentException("폰트를 찾을 수 없습니다."));

            java.awt.Font customFont = null;
            File fontFile = null;
            if (fontMeta.getTtfUrl() != null && !fontMeta.getTtfUrl().isEmpty()) {
                fontFile = new File("./uploads/fonts/" + fontMeta.getTtfUrl());
                if (fontFile.exists()) {
                    customFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new FileInputStream(fontFile));
                }
            }
            if (customFont == null && fontMeta.getOtfUrl() != null && !fontMeta.getOtfUrl().isEmpty()) {
                fontFile = new File("./uploads/fonts/" + fontMeta.getOtfUrl());
                if (fontFile.exists()) {
                    customFont = java.awt.Font.createFont(java.awt.Font.TYPE1_FONT, new FileInputStream(fontFile));
                }
            }
            if (customFont == null) {
                throw new IOException("사용자 폰트 파일을 찾을 수 없습니다.");
            }

            // 3. 이미지 그리기
            BufferedImage outputImage = new BufferedImage(
                    bgImage.getWidth(), bgImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(bgImage, 0, 0, null);

            // 폰트 설정
            customFont = customFont.deriveFont(java.awt.Font.PLAIN, 64f);
            g2d.setFont(customFont);

            // 연한 회색 설정
            g2d.setColor(new Color(153, 153, 153)); // #999999

            // 중앙 정렬
            FontMetrics metrics = g2d.getFontMetrics(customFont);
            int textWidth = metrics.stringWidth(request.getPhrase());
            int x = (bgImage.getWidth() - textWidth) / 2;
            int y = bgImage.getHeight() / 2;

            // 텍스트 그리기
            g2d.drawString(request.getPhrase(), x, y);
            g2d.dispose();


            // 4. 이미지 저장
            String fileName = "sheet_" + UUID.randomUUID() + ".png";
            File outputFile = new File("./uploads/preview/" + fileName);
            ImageIO.write(outputImage, "png", outputFile);

            // 5. DB 저장
            PracticeSheet sheet = PracticeSheet.builder()
                    .userId(request.getUserId())
                    .fontId(request.getFontId())
                    .backgroundId(request.getBackgroundId())
                    .phrase(request.getPhrase())
                    .imageUrl("/preview/" + fileName)
                    .createdAt(LocalDateTime.now())
                    .build();
            practiceSheetRepository.save(sheet);

            return PracticeSheetResponse.builder()
                    .sheetId(sheet.getSheetId())
                    .imageUrl("/preview/" + fileName)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("연습장 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }


    //연습장 다운로드
    @GetMapping("/{sheetId}/download")
    public ResponseEntity<Resource> downloadPracticeSheet(@PathVariable Integer sheetId) {
        PracticeSheet sheet = practiceSheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 연습장을 찾을 수 없습니다."));

        String fileName = sheet.getImageUrl().replace("/preview/", "");  // sheet_xxx.png
        File file = new File("./uploads/preview/" + fileName);  // 정확한 경로 연결

        if (!file.exists()) {
            throw new IllegalArgumentException("연습장 이미지 파일을 찾을 수 없습니다: " + file.getAbsolutePath());
        }

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(file.getName())
                .build());

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }



}
