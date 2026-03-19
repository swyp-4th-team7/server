package com.swyp.server.domain.family.controller;

import com.swyp.server.domain.family.dto.ConnectRequest;
import com.swyp.server.domain.family.dto.ConnectedMembersResponse;
import com.swyp.server.domain.family.service.FamilyRelationService;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Family", description = "가족 연결 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/family")
public class FamilyRelationController {

    private final FamilyRelationService familyRelationService;

    @Operation(summary = "초대코드로 사용자 연결")
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<Void>> connectByInviteCode(
            @AuthenticationPrincipal Long userId, @RequestBody @Valid ConnectRequest request) {
        familyRelationService.connectByInviteCode(userId, request.inviteCode());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "연결된 사용자 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<ConnectedMembersResponse>> getConnectedMembers(
            @AuthenticationPrincipal Long userId) {
        List<User> connectedMembers = familyRelationService.getConnectedMembers(userId);
        return ResponseEntity.ok(
                ApiResponse.success(ConnectedMembersResponse.from(connectedMembers)));
    }

    @Operation(summary = "특정 사용자와 연결 끊기")
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<ApiResponse<Void>> disconnect(
            @AuthenticationPrincipal Long userId, @PathVariable Long targetUserId) {
        familyRelationService.disconnect(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
