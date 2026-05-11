package com.portfolio.user.mapper;

import com.portfolio.user.constants.AppConstants;
import com.portfolio.user.dto.request.CreateUserProfileRequest;
import com.portfolio.user.dto.request.UpdatePreferenceRequest;
import com.portfolio.user.dto.request.UpdateProfileRequest;
import com.portfolio.user.dto.response.UserPreferenceResponse;
import com.portfolio.user.dto.response.UserProfileResponse;
import com.portfolio.user.entity.UserPreference;
import com.portfolio.user.entity.UserProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // ── CreateUserProfileRequest → UserProfile ──────────────────
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "phone",     ignore = true)
    @Mapping(target = "timezone",  constant = AppConstants.DEFAULT_TIMEZONE)
    @Mapping(target = "currency",  constant = AppConstants.DEFAULT_CURRENCY)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active",    ignore = true)
    UserProfile toUserProfile(CreateUserProfileRequest request);

    // ── UserProfile → UserProfileResponse ───────────────────────
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);

    // ── Update UserProfile from UpdateProfileRequest ─────────────
    // IGNORE nulls — only update fields that are provided
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "userId",    ignore = true)
    @Mapping(target = "email",     ignore = true)   // email not updatable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active",    ignore = true)
    void updateProfileFromRequest(UpdateProfileRequest request,
                                  @MappingTarget UserProfile userProfile);

    // ── UserPreference → UserPreferenceResponse ──────────────────
    UserPreferenceResponse toUserPreferenceResponse(UserPreference preference);

    // ── Update UserPreference from UpdatePreferenceRequest ────────
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "userId",    ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePreferenceFromRequest(UpdatePreferenceRequest request,
                                     @MappingTarget UserPreference preference);
}
