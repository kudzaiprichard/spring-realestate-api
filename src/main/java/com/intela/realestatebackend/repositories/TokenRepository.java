package com.intela.realestatebackend.repositories;

import com.intela.realestatebackend.models.Token;
import com.intela.realestatebackend.models.User;
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

    Optional<Token> findByToken(String token);
    @Query("SELECT t.user FROM Token t WHERE t.accessToken = :accessToken")
    User findUserByAccessToken(@Param("accessToken") String accessToken);

    @Query("SELECT t.user FROM Token t WHERE t.refreshToken = :refreshToken")
    User findUserByRefreshToken(@Param("refreshToken") String refreshToken);
}
