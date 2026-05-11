package com.portfolio.user.repository;

import com.portfolio.user.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(Long userId);

    Optional<UserProfile> findByEmail(String email);

    boolean existsByUserId(Long userId);

    boolean existsByEmail(String email);

    // Admin use — list all active users paginated
    @Query("SELECT u FROM UserProfile u WHERE u.active = true")
    Page<UserProfile> findAllActive(Pageable pageable);

    // Soft delete — mark inactive
    @Query("UPDATE UserProfile u SET u.active = false WHERE u.userId = :userId")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void softDeleteByUserId(@Param("userId") Long userId);
}
