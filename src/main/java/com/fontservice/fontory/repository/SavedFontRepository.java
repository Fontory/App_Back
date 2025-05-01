package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Font;
import com.fontservice.fontory.domain.SavedFont;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedFontRepository extends JpaRepository<SavedFont, Integer> {
    boolean existsByUserIdAndFontId(String userId, Integer fontId);

    //로그인된 사용자가 저장한 폰트 조회
    @Query("SELECT f FROM Font f WHERE f.fontId IN (SELECT s.fontId FROM SavedFont s WHERE s.userId = :userId)")
    List<Font> findSavedFontsByUserId(@Param("userId") String userId);


}
