package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.Post;
import com.fontservice.fontory.domain.PostLike;
import com.fontservice.fontory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 사용자가 이미 좋아요를 눌렀는지 확인
    Optional<PostLike> findByPostAndUser(Post post, User user);

    // 게시글의 총 좋아요 수
    int countByPost(Post post);

    // 특정 게시글 좋아요 전부 삭제 (ex. 게시글 삭제 시)
    void deleteAllByPost(Post post);

    @Query("SELECT pl FROM PostLike pl JOIN FETCH pl.post WHERE pl.user = :user")
    List<PostLike> findByUser(@Param("user") User user);
}