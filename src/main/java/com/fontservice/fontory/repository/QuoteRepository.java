package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // 가장 최근 문구 한 개 가져오기
    Quote findTopByOrderByCreatedAtDesc();
}