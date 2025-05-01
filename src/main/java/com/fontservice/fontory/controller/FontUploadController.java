package com.fontservice.fontory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/fonts")
@RequiredArgsConstructor
public class FontUploadController {

    @PostMapping("/upload-handwriting")
    public ResponseEntity<?> uploadHandwritingImage(
            @RequestParam("userId") String userId,
            @RequestParam("image") MultipartFile imageFile
    ) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("이미지 파일이 비어있습니다.");
        }

        try {
            String ext = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            String fileName = userId + "_" + UUID.randomUUID() + "." + ext;

            // 절대 경로로 지정
            String rootPath = System.getProperty("user.dir");
            String savePath = rootPath + "/uploads/handwriting/";

            File dir = new File(savePath);
            if (!dir.exists()) dir.mkdirs();

            File dest = new File(savePath + fileName);
            imageFile.transferTo(dest);

            // AI 처리용 경로 또는 저장 위치 반환
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("fileName", fileName);
            response.put("filePath", "/handwriting/" + fileName); // Web 접근용
            response.put("localPath", savePath + fileName);       // AI 시스템용

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 저장 실패: " + e.getMessage());
        }
    }
}
