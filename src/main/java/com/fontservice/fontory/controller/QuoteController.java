package com.fontservice.fontory.controller;

import com.fontservice.fontory.domain.Quote;
import com.fontservice.fontory.dto.QuoteResponse;
import com.fontservice.fontory.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteRepository quoteRepository;

    @GetMapping("/today")
    public QuoteResponse getTodayQuote() {
        Quote quote = quoteRepository.findRandomQuote();

        return QuoteResponse.builder()
                .quoteId(quote.getQuoteId())
                .content(quote.getContent())
                .build();
    }
}
