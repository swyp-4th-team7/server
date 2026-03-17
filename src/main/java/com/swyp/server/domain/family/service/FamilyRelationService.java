package com.swyp.server.domain.family.service;

import com.swyp.server.domain.family.entity.FamilyRelation;
import com.swyp.server.domain.family.repository.FamilyRelationRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FamilyRelationService {

    private final FamilyRelationRepository familyRelationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void connectByInviteCode(Long userId, String inviteCode) {

        User requestUser =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User targetUser =
                userRepository
                        .findByInviteCode(inviteCode)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));

        if (requestUser.getId().equals(targetUser.getId())) {
            throw new CustomException(ErrorCode.SELF_INVITE_NOT_ALLOWED);
        }

        if (familyRelationRepository.existsByOwnerUserIdAndTargetUserId(
                requestUser.getId(), targetUser.getId())) {
            throw new CustomException(ErrorCode.ALREADY_CONNECTED_FAMILY_MEMBER);
        }

        familyRelationRepository.save(
                FamilyRelation.builder().ownerUser(requestUser).targetUser(targetUser).build());
        familyRelationRepository.save(
                FamilyRelation.builder().ownerUser(targetUser).targetUser(requestUser).build());
    }

    @Transactional(readOnly = true)
    public List<User> getConnectedMembers(Long userId) {
        List<FamilyRelation> relations = familyRelationRepository.findAllByOwnerUserId(userId);
        return relations.stream().map(FamilyRelation::getTargetUser).toList();
    }

    @Transactional
    public void disconnect(Long userId, Long targetUserId) {
        if (!familyRelationRepository.existsByOwnerUserIdAndTargetUserId(userId, targetUserId)) {
            throw new CustomException(ErrorCode.ALREADY_CONNECTED_FAMILY_MEMBER);
        }

        familyRelationRepository.deleteByOwnerUserIdAndTargetUserId(userId, targetUserId);
        familyRelationRepository.deleteByOwnerUserIdAndTargetUserId(targetUserId, userId);
    }
}
