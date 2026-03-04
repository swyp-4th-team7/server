package com.swyp.server.global.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityPath {

    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**", "/swagger-ui/**", "/api-docs/**", "/swagger-ui.html"
    };
}
