package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirstName(String name);

    User getUserByEmail(String email);

    boolean existsByEmailAndMobile(String email, String Mobile);
}
