package com.portfolio.user.controller;

import com.portfolio.user.constants.AppConstants;
import com.portfolio.user.dto.request.CreateUserProfileRequest;
import com.portfolio.user.dto.request.UpdatePreferenceRequest;
import com.portfolio.user.dto.request.UpdateProfileRequest;
import com.portfolio.user.dto.response.ApiResponse;
import com.portfolio.user.dto.response.UserPreferenceResponse;
import com.portfolio.user.dto.response.UserProfileResponse;
import com.portfolio.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.USER_BASE_URL)
@Tag(name = "User Service", description = "User profile and preferences management")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get own profile")
    @GetMapping(AppConstants.PROFILE_URL)
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @RequestHeader(AppConstants.HEADER_USER_ID) Long userId) {

        UserProfileResponse profile = userService.getMyProfile(userId);
        return ResponseEntity.ok(
                ApiResponse.success(profile, "Profile fetched successfully"));
    }

    @Operation(summary = "Update own profile")
    @PutMapping(AppConstants.PROFILE_URL)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @RequestHeader(AppConstants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserProfileResponse updated = userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Profile updated successfully"));
    }

    @Operation(summary = "Get own notification preferences")
    @GetMapping(AppConstants.PREFERENCES_URL)
    public ResponseEntity<ApiResponse<UserPreferenceResponse>> getMyPreferences(
            @RequestHeader(AppConstants.HEADER_USER_ID) Long userId) {

        UserPreferenceResponse prefs = userService.getMyPreferences(userId);
        return ResponseEntity.ok(
                ApiResponse.success(prefs, "Preferences fetched successfully"));
    }

    @Operation(summary = "Update notification preferences")
    @PutMapping(AppConstants.PREFERENCES_URL)
    public ResponseEntity<ApiResponse<UserPreferenceResponse>> updateMyPreferences(
            @RequestHeader(AppConstants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody UpdatePreferenceRequest request) {

        UserPreferenceResponse updated =
                userService.updateMyPreferences(userId, request);
        return ResponseEntity.ok(
                ApiResponse.success(updated, "Preferences updated successfully"));
    }

    @Operation(summary = "Get user by ID — internal Feign call from portfolio-service")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
            @PathVariable Long userId) {

        UserProfileResponse profile = userService.getUserById(userId);
        return ResponseEntity.ok(
                ApiResponse.success(profile, "User fetched successfully"));
    }

    @Operation(summary = "Create user profile — internal call from auth-service")
    @PostMapping("/internal/create")
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequest request) {

        UserProfileResponse created = userService.createUserProfile(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.created(created,
                        "User profile created successfully"));
    }

    @Operation(summary = "List all users — Admin only")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getAllUsers(
            Pageable pageable) {

        Page<UserProfileResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(
                ApiResponse.success(users, "Users fetched successfully"));
    }

    @Operation(summary = "Soft-delete user — Admin only")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId) {

        userService.deleteUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "User deleted successfully"));
    }
}
