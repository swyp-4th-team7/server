package com.swyp.server.domain.family.repository;

import com.swyp.server.domain.family.entity.FamilyRelation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRelationRepository extends JpaRepository<FamilyRelation, Long> {
    boolean existsByOwnerUserIdAndTargetUserId(Long ownerUserId, Long targetUserId);

    Optional<FamilyRelation> findByOwnerUserIdAndTargetUserId(Long ownerUserId, Long targetUserId);

    @EntityGraph(attributePaths = "targetUser")
    List<FamilyRelation> findAllByOwnerUserId(Long ownerUserId);

    void deleteByOwnerUserIdAndTargetUserId(Long ownerUserId, Long targetUserId);
}
