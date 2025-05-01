package com.fontservice.fontory.dto.sheet;

import com.fontservice.fontory.domain.PracticeSheet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class PracticeSheetResponse {
    private Integer sheetId;
    private String imageUrl;
    private String phrase;
    private Integer fontId;
    private Integer backgroundId;
    private LocalDateTime createdAt;

    public PracticeSheetResponse(PracticeSheet sheet) {
        this.sheetId = sheet.getSheetId();
        this.imageUrl = sheet.getImageUrl();
        this.phrase = sheet.getPhrase();
        this.fontId = sheet.getFontId();
        this.backgroundId = sheet.getBackgroundId();
        this.createdAt = sheet.getCreatedAt();
    }
}
