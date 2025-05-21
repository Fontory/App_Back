package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Font;
import com.fontservice.fontory.domain.FontLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FontLikeRepository extends JpaRepository<FontLike, Integer> {

    //좋아요 중복 확인용
    boolean existsByUserIdAndFontId(String userId, Integer fontId);

    //좋아요 취소
    Optional<FontLike> findByUserIdAndFontId(String userId, Integer fontId);

    // 로그인된 사용자가 좋아요한 폰트 조회
    @Query("SELECT f FROM Font f WHERE f.fontId IN (SELECT fl.fontId FROM FontLike fl WHERE fl.userId = :userId)")
    List<Font> findLikedFontsByUserId(@Param("userId") String userId);
}
