package com.RestAPI.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.RestAPI.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
        SELECT t FROM Token t INNER JOIN User u
        ON t.user.id = u.id
        WHERE t.user.id = :userId AND t.isLoggedOut = FALSE
    """)
    List<Token> findAllTokenByUser(Long userId); // TODO findAllTokenByUser_Id(Long id)

    Optional<Token> findByToken(String token);
}
