package com.example.collaborativetools.global.constant;

public enum UserRoleEnum {
    USER(Authority.USER),  // 개인 권한
    ADMIN(Authority.ADMIN);  // 기업 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
