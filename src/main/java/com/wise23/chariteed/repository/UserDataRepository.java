package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
    Optional<UserData> findByFirstName(String name);

    UserData getUserDataByEmail(String email);

    boolean existsByEmailAndMobile(String email, String Mobile);

    List<UserData> findByRole(Role role);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserData u WHERE u.firstName = :firstName AND u.lastName = :lastName AND u.mobile = :mobile")
    UserData deleteByFullNameAndMobile(@Param("firstName") String firstName, @Param("lastName") String lastName,
            @Param("mobile") String mobile);
}
