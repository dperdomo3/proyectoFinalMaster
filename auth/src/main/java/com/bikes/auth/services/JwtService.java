package com.bikes.auth.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.duration}")
    private Integer duration;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(key.getBytes());
        this.verifier = JWT.require(algorithm).build();
    }

    public String generateToken(String subject, String role) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .withClaim("role", role.toUpperCase())
                .sign(algorithm);
    }

    public String getRoleFromToken(String token) {
        return verifier.verify(token).getClaim("role").asString();
    }

    public String getSubjectFromToken(String token) {
        return verifier.verify(token).getSubject();
    }

    public String extractToken(String header) {
        return header.substring("Bearer ".length());
    }
}
