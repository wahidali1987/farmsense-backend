package com.farmsense.repository;

import com.farmsense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // 🔐 Login ke liye
    Optional<User> findByEmail(String email);

    // ✅ Validation (registration / update)
    boolean existsByEmail(String email);

    // 🔍 Active user check (future use)
    Optional<User> findByEmailAndIsActiveTrue(String email);
}
