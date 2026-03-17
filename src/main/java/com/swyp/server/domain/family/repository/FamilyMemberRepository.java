package com.swyp.server.domain.family.repository;

import com.swyp.server.domain.family.entity.FamilyMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    Optional<FamilyMember> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<FamilyMember> findAllByFamilyId(Long familyId);

    long countByFamilyId(Long familyId);

    void deleteByUserId(Long userId);
}
