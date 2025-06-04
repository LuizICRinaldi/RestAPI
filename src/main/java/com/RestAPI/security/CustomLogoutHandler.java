package com.RestAPI.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.RestAPI.entity.Token;
import com.RestAPI.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        
        if(authHeader == null || !authHeader.startsWith("Bearer "))
            return;

        String token = authHeader.replace("Bearer ", "");
        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        if(token != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }
    }
}
