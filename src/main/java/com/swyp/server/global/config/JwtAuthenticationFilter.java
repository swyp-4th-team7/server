package com.swyp.server.global.config;

import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import com.swyp.server.global.exception.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final AntPathMatcher matcher = new AntPathMatcher();

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                jwtProvider.validateToken(token);
                Long userId = jwtProvider.getUserIdFromToken(token);

                // 탈퇴한 유저의 토큰 접근차단
                if (!userRepository.existsById(userId)) {
                    throw new CustomException(ErrorCode.USER_NOT_FOUND);
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (CustomException e) {
                log.error("JWT authentication failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                throw new JwtAuthenticationException(e.getErrorCode(), e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();

        for (String publicUrl : SecurityPath.PUBLIC_URLS) {
            if (matcher.match(publicUrl, uri)) {
                return true;
            }
        }
        return false;
    }
}
