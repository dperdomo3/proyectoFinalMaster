package com.bikes.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bikes.auth.services.JwtService;
import com.bikes.auth.domain.TokenValidationResponse;
import com.bikes.auth.model.Role;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtService jwtService;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken(
            @RequestParam String subject,
            @RequestParam String role) {
        try {
            Role.valueOf(role.toUpperCase()); // valida que sea ADMIN o APARCAMIENTO
            String token = jwtService.generateToken(subject, role);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Rol inválido. Usa: ADMIN o APARCAMIENTO.");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            String sub = jwtService.getSubjectFromToken(token);
            String role = jwtService.getRoleFromToken(token);
            return ResponseEntity.ok(new TokenValidationResponse(sub, role));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
        }
    }

}
