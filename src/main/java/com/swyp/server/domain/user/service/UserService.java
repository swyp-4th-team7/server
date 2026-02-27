package com.swyp.server.domain.user.service;

import com.swyp.server.domain.user.entity.Role;
import com.swyp.server.domain.user.entity.User;
import com.swyp.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrCreateUser(String email, String nickname, String profileImageUrl) {
        return userRepository
                .findByEmail(email)
                .orElseGet(
                        () ->
                                userRepository.save(
                                        User.builder()
                                                .email(email)
                                                .nickname(nickname)
                                                .profileImageUrl(profileImageUrl)
                                                .role(Role.USER)
                                                .build()));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
