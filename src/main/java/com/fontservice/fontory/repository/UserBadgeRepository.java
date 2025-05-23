package com.fontservice.fontory.repository;

import com.fontservice.fontory.domain.User;
import com.fontservice.fontory.domain.UserBadge;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {

    List<UserBadge> findByUser(User user);

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user = :user")
    List<UserBadge> findByUserWithBadge(@Param("user") User user);
}