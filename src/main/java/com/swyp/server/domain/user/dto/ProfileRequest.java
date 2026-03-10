package com.swyp.server.domain.user.dto;

import com.swyp.server.domain.user.entity.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank(message = "NICKNAME_REQUIRED")
                @Size(min = 1, max = 8, message = "NICKNAME_LENGTH_INVALID")
                @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "NICKNAME_PATTERN_INVALID")
                String nickname,
        @NotNull(message = "USER_TYPE_REQUIRED") UserType userType) {}
