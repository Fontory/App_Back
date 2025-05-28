package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.dto.font.FontCreateRequest;
import com.fontservice.fontory.service.FontService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/fonts")
@RequiredArgsConstructor
public class FontCreateController {

    private final FontService fontService;

    //128*128로 쪼개 압축폴더 반환
    @PostMapping("/create")
    public ResponseEntity<?> uploadHandwritingImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("fontName") String fontName,
            HttpServletRequest httpRequest
    ) {
        try {
            //1. 사용자 인증
            User sessionUser = (User) httpRequest.getSession().getAttribute("user");
            String userId = sessionUser != null ? sessionUser.getUserId() : null;
            if (userId == null) {
                return ResponseEntity.status(401).body("로그인 후 이용 가능합니다.");
            }

            //2. 원본 이미지 저장
            String timestamp = String.valueOf(System.currentTimeMillis());
            String relativePath = "/handwriting/" + userId + "/" + timestamp; // 클라이언트에 반환될 경로
            String absolutePath = System.getProperty("user.dir") + "/uploads" + relativePath;
            new File(absolutePath).mkdirs();

            //원본 이미지 저장
            String originalFileName = "original.png";
            String originalImageAbsolutePath = absolutePath + "/" + originalFileName;
            imageFile.transferTo(new File(originalImageAbsolutePath));

            BufferedImage originalImage = ImageIO.read(new File(originalImageAbsolutePath));
            if (originalImage == null) {
                return ResponseEntity.badRequest().body("이미지 파일을 읽을 수 없습니다.");
            }

            //3. 이미지 80개 분할 및 저장
            List<String> fileNames = Arrays.asList(
                    "갓", "같", "걓", "곬", "깼", "꺡", "낥", "냥", "넊", "넋", "녊", "놊", "닫", "닭", "덎", "덳",
                    "돳", "땛", "략", "렠", "롅", "뢅", "룅", "몃", "몉", "몮", "뫮", "묮", "밟", "볘", "볲", "뵗",
                    "뺐", "뽈", "솨", "솩", "쇩", "쐐", "쐒", "쑒", "앉", "않", "얘", "얾", "왻", "욻", "융", "죡",
                    "죤", "줤", "즉", "쭍", "쮜", "춰", "춶", "췶", "츄", "츶", "칛", "칸", "퀟", "퀭", "큟", "킞",
                    "탡", "튈", "틈", "틔", "팈", "팥", "퍊", "퓱", "픱", "핀", "핥", "햎", "햳", "훟", "흚", "힚"
            );

            int cellWidth = 66;
            int cellHeight = 66;
            double cellXGap = 75.8;
            double cellYGap = 173.8;
            int startX = 27;
            int startY = 61;


            for (int i = 0; i < fileNames.size(); i++) {
                int row = i / 16;
                int col = i % 16;
                int x = (int) Math.round(startX + col * cellXGap);
                int y = (int) Math.round(startY + row * cellYGap);

                BufferedImage subImage = originalImage.getSubimage(x, y, cellWidth, cellHeight);
                BufferedImage resizedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resizedImage.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(subImage, 0, 0, 128, 128, null);
                g.dispose();

                String fileName = fileNames.get(i) + ".png";
                File outputFile = new File(absolutePath + "/" + fileName);
                ImageIO.write(resizedImage, "png", outputFile);
            }

            // 3. AI 모델 호출 (ProcessBuilder)
            //테스트용 더미데이터
            String ttfPath = "/fonts/딸에게 엄마가.ttf";
            String otfPath = "/fonts/딸에게 엄마가.ttf";
            float vectorSimilarity = 0.852f;

            /*
            String outputDir = "./uploads/fonts/generated/" + userId + "/" + timestamp;
            new File(outputDir).mkdirs();

            ProcessBuilder pb = new ProcessBuilder(
                    "python", "font_generator.py",
                    "--font-name", fontName,
                    "--input-dir", imageSaveDir,
                    "--output-dir", outputDir
            );
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return ResponseEntity.status(500).body("AI 모델 실행 실패");
            }

            File ttfFile = new File(outputDir + "/generated.ttf");
            File otfFile = new File(outputDir + "/generated.otf");
            File simFile = new File(outputDir + "/vector_similarity.json");
            if (!ttfFile.exists() || !simFile.exists()) {
                return ResponseEntity.status(500).body("AI 결과 파일 생성 실패");
            }
            float vectorSimilarity = new Gson().fromJson(new FileReader(simFile), JsonObject.class).get("similarity").getAsFloat();
             */

            // 5. DB 저장 (originalImagePath는 Web URL 경로로 저장)
            int fontId = fontService.createFont(
                    userId, fontName, ttfPath, otfPath, relativePath + "/" + originalFileName, vectorSimilarity
            );

            // 5. 프론트 반환
            Map<String, Object> response = new HashMap<>();
            response.put("fontId", fontId);
            response.put("fontName", fontName);
            response.put("ttfUrl", ttfPath);
            response.put("otfUrl", otfPath);
            response.put("vectorSimilarity", vectorSimilarity);
            response.put("status", "success");
            response.put("cellImagesPath", relativePath); // 프론트에서는 "/handwriting/..." 경로로 접근 가능

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("폰트 생성 실패: " + e.getMessage());
        }
    }


    /*
    //폰트 등록
    @PostMapping("/create")
    public ResponseEntity<?> createFont(@RequestBody FontCreateRequest request, HttpServletRequest httpRequest) {
        try {
            User sessionUser = (User) httpRequest.getSession().getAttribute("user");
            String userId = sessionUser != null ? sessionUser.getUserId() : null;

            if (userId == null) {
                return ResponseEntity.status(401).body("로그인 후 이용 가능합니다.");
            }

            int fontId = fontService.createFont(request, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("fontId", fontId);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("폰트 저장 실패: " + e.getMessage());
        }
    }
    */


}
