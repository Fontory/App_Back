package com.fontservice.fontory.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuoteResponse {
    private Integer quoteId;
    private String content;
    private LocalDateTime createdAt;
}
