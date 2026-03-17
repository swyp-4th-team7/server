package com.swyp.server.domain.family.service;

import com.swyp.server.domain.family.entity.Family;
import com.swyp.server.domain.family.entity.FamilyMember;
import com.swyp.server.domain.family.repository.FamilyMemberRepository;
import com.swyp.server.domain.family.repository.FamilyRepository;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import com.swyp.server.global.exception.CustomException;
import com.swyp.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public void joinFamilyByInviteCode(Long userId, String inviteCode) {
        if (familyMemberRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.ALREADY_IN_FAMILY);
        }

        User owner =
                userRepository
                        .findByInviteCode(inviteCode)
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INVITE_CODE));

        if (owner.getId().equals(userId)) {
            throw new CustomException(ErrorCode.SELF_INVITE_NOT_ALLOWED);
        }

        Family family =
                familyMemberRepository
                        .findByUserId(owner.getId())
                        .map(FamilyMember::getFamily)
                        .orElseGet(() -> createFamilyWithOwner(owner));

        User user = userRepository.getReferenceById(userId);
        FamilyMember newMember = FamilyMember.builder().family(family).user(user).build();
        familyMemberRepository.save(newMember);
    }

    private Family createFamilyWithOwner(User owner) {
        Family family = familyRepository.save(Family.create());

        FamilyMember ownerMember = FamilyMember.builder().family(family).user(owner).build();
        familyMemberRepository.save(ownerMember);
        return family;
    }
}
