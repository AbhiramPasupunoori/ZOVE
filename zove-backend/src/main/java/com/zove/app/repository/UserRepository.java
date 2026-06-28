package com.zove.app.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zove.app.model.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findTop20ByUsernameContainingIgnoreCaseOrDisplayNameContainingIgnoreCaseOrderByUsernameAsc(
            String username,
            String displayName
    );
}
