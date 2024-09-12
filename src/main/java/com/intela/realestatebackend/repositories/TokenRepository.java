package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.Token;
import com.intela.realestatebackend.models.User;
import com.intela.realestatebackend.models.archetypes.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
                select t from tokens t inner join users  u on t.user.id = u.id
                where u.id = :userId and (t.expired = false or t.revoked = false)
            """)
    List<Token> findAllValidTokenByUser(Integer userId);

    Optional<Token> findByTokenAndExpiredFalseAndRevokedFalse(String token);

    @Query("SELECT u\n" +
            "FROM users u\n" +
            "JOIN tokens t\n" +
            "WHERE t.token = :accessToken AND t.revoked = false AND t.expired = false")
    User findUserByAccessToken(@Param("accessToken") String accessToken);

    @Query("SELECT u\n" +
            "FROM users u\n" +
            "JOIN tokens t\n" +
            "WHERE t.token = :refreshToken AND t.revoked = false AND t.expired = false")
    User findUserByRefreshToken(@Param("refreshToken") String refreshToken);

    @Query("SELECT t FROM tokens t WHERE t.user = :user AND t.tokenType = :tokenType")
    Optional<Token> findTokenByUserAndType(@Param("user") User user, @Param("tokenType") TokenType tokenType);
}
