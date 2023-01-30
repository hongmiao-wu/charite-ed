package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirstName(String name);

    User getUserByEmail(String email);

    boolean existsByEmailAndMobile(String email, String Mobile);

    List<User> findByRole(Role role);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName AND u.mobile = :mobile")
    void deleteByFullNameAndMobile(@Param("firstName") String firstName, @Param("lastName") String lastName,
            @Param("mobile") String mobile);
}
