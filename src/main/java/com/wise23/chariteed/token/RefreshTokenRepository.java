package com.wise23.chariteed.token;
import com.wise23.chariteed.token.RefreshToken;
import org.checkerframework.framework.qual.RequiresQualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    RefreshToken deleteRefreshTokenByUserId(@Param("userId") Long userId);
}
