package com.RestAPI.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.RestAPI.entity.Token;
import com.RestAPI.entity.User;
import com.RestAPI.repository.TokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withIssuer("RestAPI")
                .withSubject(user.getEmail())
                .withExpiresAt(generateExpirationDate())
                .sign(algorithm);

            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            boolean isValidToken = tokenRepository.findByToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);
            
            return isValidToken ? JWT.require(algorithm)
                .withIssuer("RestAPI")
                .build()
                .verify(token)
                .getSubject() : "";
        } catch (JWTVerificationException e) {
            return "";
        }
    }   

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public void save(Token token) {
        tokenRepository.save(token);
    }

    public void findAllTokenByUser(Long id) {
        List<Token> tokens = tokenRepository.findAllTokenByUser(id);

        if(!tokens.isEmpty()) {
            tokens.forEach(token -> {
                token.setLoggedOut(true);
            });
        }

        tokenRepository.saveAll(tokens);
    }
}
