package com.bikes.gateway.filter;

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

@Component
public class GatewayFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("Interceptando: " + method + " " + path);

        // RUTAS PÚBLICAS
        boolean esPublica = (path.equals("/aparcamientos") && method.equals("GET")) ||
                (path.matches("/aparcamiento/\\d+/status") && method.equals("GET")) ||
                (path.matches("/aparcamiento/\\d+/status") && path.contains("from=") && path.contains("to=")
                        && method.equals("GET"))
                ||
                (path.equals("/evento/top10-disponibles") && method.equals("GET")) ||
                path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.startsWith("/api-docs");

        if (esPublica) {
            System.out.println("🔓 Ruta pública permitida sin autenticación");
            filterChain.doFilter(request, response);
            return;
        }

        // Resto requiere autorización
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                ResponseEntity<String> res = restTemplate.exchange(
                        "http://localhost:8081/api/auth/validate?token=" + token,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders() {
                            {
                                setContentType(MediaType.APPLICATION_JSON);
                            }
                        }),
                        String.class);

                JsonNode json = objectMapper.readTree(res.getBody());
                String userId = json.get("subject").asText();
                String role = json.get("role").asText();

                request.setAttribute("X-User-Id", userId);
                request.setAttribute("X-User-Role", role);

                // Reglas por rol
                if (path.equals("/aparcamiento") && method.equals("POST") && !role.equalsIgnoreCase("ADMIN")) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Solo ADMIN puede crear aparcamientos.");
                    return;
                }

                if (path.matches("/aparcamiento/\\d+") && method.equals("DELETE") && !role.equalsIgnoreCase("ADMIN")) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Solo ADMIN puede eliminar aparcamientos.");
                    return;
                }

                if (path.matches("/aparcamiento/\\d+") && method.equals("PUT") && !role.equalsIgnoreCase("ADMIN")) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Solo ADMIN puede editar aparcamientos.");
                    return;
                }

                if (path.matches("/evento/\\d+") && method.equals("POST") && !role.equalsIgnoreCase("APARCAMIENTO")) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Solo APARCAMIENTO puede registrar eventos.");
                    return;
                }

            } catch (Exception e) {
                System.out.println("Token inválido: " + e.getMessage());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Token inválido");
                return;
            }

        } else {
            System.out.println("⚠ Falta encabezado Authorization");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Falta Authorization");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
