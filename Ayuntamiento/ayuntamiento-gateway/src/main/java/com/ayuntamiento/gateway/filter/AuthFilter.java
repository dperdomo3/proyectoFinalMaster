package com.ayuntamiento.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String AUTH_SERVICE_VALIDATE_URL = "http://localhost:8081/api/auth/validate?token=";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("🔐 Interceptando: " + request.getMethod() + " " + path);

        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token faltante o inválido");
            return;
        }

        String token = authHeader.substring(7);
        try {
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
            ResponseEntity<String> validationResponse = restTemplate.exchange(
                    AUTH_SERVICE_VALIDATE_URL + encodedToken,
                    HttpMethod.GET,
                    null,
                    String.class);

            JsonNode json = objectMapper.readTree(validationResponse.getBody());
            String userId = json.get("subject").asText();
            String role = json.get("role").asText();

            request.setAttribute("X-User-Id", userId);
            request.setAttribute("X-User-Role", role);
            System.out.println("✅ Token válido. Rol: " + role);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o error de validación: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return path.startsWith("/api/ayuntamiento/aggregatedData") ||
                path.startsWith("/api/ayuntamiento/aggregateData") ||
                path.startsWith("/api/ayuntamiento/aparcamientoCercano") ||
                path.startsWith("/api/ayuntamiento/ping") ||
                path.startsWith("/api/aparcamientos") ||
                path.startsWith("/api/estaciones") ||
                (path.startsWith("/api/estacion/") && path.contains("/status")) ||
                (path.startsWith("/api/aparcamiento/") && path.endsWith("/status")) ||
                path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.startsWith("/api-docs");

    }

}
