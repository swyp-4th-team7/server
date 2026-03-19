package com.swyp.server.domain.user.repository;

import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.entity.UserType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(
            value = "SELECT * FROM users WHERE deleted_at IS NOT NULL AND deleted_at <= :cutoff",
            nativeQuery = true)
    List<User> findAllDeletedBefore(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT u FROM User u")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE u.userType = :userType")
    List<User> findAllActiveByUserType(@Param("userType") UserType userType);
}
