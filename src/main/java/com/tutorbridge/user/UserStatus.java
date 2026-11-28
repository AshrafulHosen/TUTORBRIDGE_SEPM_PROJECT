package com.tutorbridge.user;

/**
 * Account status. A suspended user's token is rejected on the next request.
 */
public enum UserStatus {
    ACTIVE,
    SUSPENDED
}
