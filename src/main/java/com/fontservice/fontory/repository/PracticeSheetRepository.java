package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.PracticeSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeSheetRepository extends JpaRepository<PracticeSheet, Integer> {
    List<PracticeSheet> findByUserId(String userId);
}
