package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Font;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FontRepository extends JpaRepository<Font, Integer> {

    // 최신순 조회 (createdAt 기준 내림차순)
    @Query("SELECT f FROM Font f JOIN FETCH f.user WHERE f.isPublic = :isPublic ORDER BY f.createdAt DESC")
    List<Font> findWithUserByIsPublicOrderByCreatedAtDesc(@Param("isPublic") Font.PublicStatus isPublic);

    // 인기순 조회 (likeCount 기준 내림차순)
    @Query("SELECT f FROM Font f JOIN FETCH f.user WHERE f.isPublic = :isPublic ORDER BY f.likeCount DESC")
    List<Font> findWithUserByIsPublicOrderByLikeCountDesc(@Param("isPublic") Font.PublicStatus isPublic);

    // 로그인된 사용자의 비공개된 폰트 조회
    List<Font> findByUserIdAndIsPublic(String userId, Font.PublicStatus isPublic);

    //로그인된 사용자의 공개/비공개 폰트
    List<Font> findByUserId(String userId);


}
