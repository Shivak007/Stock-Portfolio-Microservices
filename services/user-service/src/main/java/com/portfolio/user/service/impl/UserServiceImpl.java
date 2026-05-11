package com.portfolio.user.service.impl;

import com.portfolio.user.constants.AppConstants;
import com.portfolio.user.dto.request.CreateUserProfileRequest;
import com.portfolio.user.dto.request.UpdatePreferenceRequest;
import com.portfolio.user.dto.request.UpdateProfileRequest;
import com.portfolio.user.dto.response.UserPreferenceResponse;
import com.portfolio.user.dto.response.UserProfileResponse;
import com.portfolio.user.entity.UserPreference;
import com.portfolio.user.entity.UserProfile;
import com.portfolio.user.exception.custom.UserAlreadyExistsException;
import com.portfolio.user.exception.custom.UserNotFoundException;
import com.portfolio.user.mapper.UserMapper;
import com.portfolio.user.repository.UserPreferenceRepository;
import com.portfolio.user.repository.UserProfileRepository;
import com.portfolio.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // ── Constructor injection — no @Autowired, no @Value fields ──
    private final UserProfileRepository    profileRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final UserMapper               userMapper;

    public UserServiceImpl(UserProfileRepository profileRepository,
                           UserPreferenceRepository preferenceRepository,
                           UserMapper userMapper) {
        this.profileRepository    = profileRepository;
        this.preferenceRepository = preferenceRepository;
        this.userMapper           = userMapper;
    }

    // ─────────────────────────────────────────────────────────────
    // CREATE — called by auth-service via Feign after registration
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserProfileResponse createUserProfile(CreateUserProfileRequest request) {
        log.info("Creating user profile for userId={}", request.userId());

        if (profileRepository.existsByUserId(request.userId())) {
            throw new UserAlreadyExistsException(
                    "Profile already exists for userId: " + request.userId());
        }
        if (profileRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(
                    "Profile already exists for email: " + request.email());
        }

        UserProfile profile = userMapper.toUserProfile(request);
        UserProfile saved   = profileRepository.save(profile);

        // Create default preferences alongside profile atomically
        UserPreference preference = UserPreference.builder()
                .userId(saved.getUserId())
                .emailNotifications(true)
                .priceAlertEmail(true)
                .dailySummaryEmail(false)
                .preferredCurrency(AppConstants.DEFAULT_CURRENCY)
                .build();
        preferenceRepository.save(preference);

        log.info("User profile created successfully for userId={}", saved.getUserId());
        return userMapper.toUserProfileResponse(saved);
    }

    // ─────────────────────────────────────────────────────────────
    // GET OWN PROFILE
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        log.debug("Fetching profile for userId={}", userId);
        UserProfile profile = findActiveProfileByUserId(userId);
        return userMapper.toUserProfileResponse(profile);
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE OWN PROFILE
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserProfileResponse updateMyProfile(Long userId,
                                               UpdateProfileRequest request) {
        log.info("Updating profile for userId={}", userId);
        UserProfile profile = findActiveProfileByUserId(userId);
        userMapper.updateProfileFromRequest(request, profile);
        UserProfile updated = profileRepository.save(profile);
        return userMapper.toUserProfileResponse(updated);
    }

    // ─────────────────────────────────────────────────────────────
    // GET PREFERENCES
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserPreferenceResponse getMyPreferences(Long userId) {
        log.debug("Fetching preferences for userId={}", userId);
        UserPreference pref = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Preferences not found for userId: " + userId));
        return userMapper.toUserPreferenceResponse(pref);
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE PREFERENCES
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserPreferenceResponse updateMyPreferences(Long userId,
                                                      UpdatePreferenceRequest request) {
        log.info("Updating preferences for userId={}", userId);
        UserPreference pref = preferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "Preferences not found for userId: " + userId));
        userMapper.updatePreferenceFromRequest(request, pref);
        UserPreference updated = preferenceRepository.save(pref);
        return userMapper.toUserPreferenceResponse(updated);
    }

    // ─────────────────────────────────────────────────────────────
    // INTERNAL — used by portfolio-service Feign
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long userId) {
        log.debug("Internal Feign call: getUserById userId={}", userId);
        UserProfile profile = findActiveProfileByUserId(userId);
        return userMapper.toUserProfileResponse(profile);
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN — list all users
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileResponse> getAllUsers(Pageable pageable) {
        log.debug("Admin: fetching all users paginated");
        return profileRepository.findAllActive(pageable)
                .map(userMapper::toUserProfileResponse);
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN — soft delete
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Admin: soft-deleting userId={}", userId);
        if (!profileRepository.existsByUserId(userId)) {
            throw new UserNotFoundException(userId);
        }
        profileRepository.softDeleteByUserId(userId);
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────
    private UserProfile findActiveProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .filter(UserProfile::isActive)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
