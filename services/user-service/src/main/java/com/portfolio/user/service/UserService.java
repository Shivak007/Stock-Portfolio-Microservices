package com.portfolio.user.service;

import com.portfolio.user.dto.request.CreateUserProfileRequest;
import com.portfolio.user.dto.request.UpdatePreferenceRequest;
import com.portfolio.user.dto.request.UpdateProfileRequest;
import com.portfolio.user.dto.response.UserPreferenceResponse;
import com.portfolio.user.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    // ── Called by auth-service via Feign after user registers ────
    UserProfileResponse createUserProfile(CreateUserProfileRequest request);

    // ── User's own profile operations ────────────────────────────
    UserProfileResponse getMyProfile(Long userId);

    UserProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request);

    // ── User's preferences ────────────────────────────────────────
    UserPreferenceResponse getMyPreferences(Long userId);

    UserPreferenceResponse updateMyPreferences(Long userId,
                                               UpdatePreferenceRequest request);

    // ── Internal Feign endpoint — used by portfolio-service ───────
    // Validates user exists before portfolio creation
    UserProfileResponse getUserById(Long userId);

    // ── Admin operations ──────────────────────────────────────────
    Page<UserProfileResponse> getAllUsers(Pageable pageable);

    void deleteUser(Long userId);
}
