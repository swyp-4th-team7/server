package com.swyp.server.global.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityPath {

    public static final List<String> PUBLIC_URLS =
            List.of(
                    "/api/v1/auth/**",
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/swagger-ui.html",
                    "/privacy.html",
                    "/terms.html");
}
