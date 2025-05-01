package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Post;
import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.enums.PostType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글 유형별 목록 조회 (ex. 필사 게시판만 보기)
    List<Post> findAllByPostTypeOrderByCreatedAtDesc(PostType type);

    // 사용자별 게시글 조회 (ex. 마이페이지 등)
    List<Post> findByUser_UserId(String userId);

    // 최신순 정렬
    List<Post> findAllByOrderByCreatedAtDesc();

    // 인기순(좋아요순) 정렬
    List<Post> findAllByOrderByLikeCountDesc();

    List<Post> findByUser(User user);

    int countByUser(User user); // 사용자가 작성한 게시글 수 카운트
}