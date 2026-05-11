package com.portfolio.user.constants;

public final class AppConstants {

    private AppConstants() {}

    // API paths
    public static final String USER_BASE_URL        = "/api/users";
    public static final String PROFILE_URL          = "/profile";
    public static final String PREFERENCES_URL      = "/preferences";

    // Header names forwarded by API Gateway after JWT validation
    public static final String HEADER_USER_ID       = "X-User-Id";
    public static final String HEADER_USER_EMAIL    = "X-User-Email";
    public static final String HEADER_USER_ROLES    = "X-User-Roles";

    // Default values
    public static final String DEFAULT_CURRENCY     = "INR";
    public static final String DEFAULT_TIMEZONE     = "Asia/Kolkata";
}
