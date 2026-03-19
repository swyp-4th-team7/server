package com.swyp.server.domain.family.dto;

import jakarta.validation.constraints.NotBlank;

public record ConnectRequest(@NotBlank(message = "INVITE_CODE_REQUIRED") String inviteCode) {}
