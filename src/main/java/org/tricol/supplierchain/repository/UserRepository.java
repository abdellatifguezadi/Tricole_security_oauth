package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tricol.supplierchain.entity.UserApp;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserApp, Long> {
    Optional<UserApp> findByUsername(String username);
    Optional<UserApp> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
