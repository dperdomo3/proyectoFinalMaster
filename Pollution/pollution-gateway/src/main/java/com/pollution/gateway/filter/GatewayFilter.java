package com.pollution.gateway.filter;

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
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("Interceptando: " + method + " " + path);

        // Define rutas públicas que no requieren autenticación
        boolean esPublica = (path.equals("/estaciones") ||
                (path.matches("/estacion/\\d+/status") && method.equals("GET")) ||
                (path.matches("/estacion/\\d+/status\\?.*") && method.equals("GET")) ||
                path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.startsWith("/api-docs") // si usas este path
        );

        if (esPublica) {
            System.out.println("Ruta pública permitida sin autenticación");
            filterChain.doFilter(request, response);
            return;
        }

        // Valida si el encabezado Authorization está presente y tiene formato Bearer
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Llama al auth-service para validar el token
                ResponseEntity<String> res = restTemplate.exchange(
                        "http://localhost:8081/api/auth/validate?token=" + token,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders() {
                            {
                                setContentType(MediaType.APPLICATION_JSON);
                            }
                        }),
                        String.class);

                // Extrae el userId y role desde el JWT validado
                JsonNode json = objectMapper.readTree(res.getBody());
                String userId = json.get("subject").asText();
                String role = json.get("role").asText();

                // Inserta como atributos del request para que el controlador los use
                request.setAttribute("X-User-Id", userId);
                request.setAttribute("X-User-Role", role);

            } catch (Exception e) {
                // Si el token es inválido, devuelve error 401
                System.out.println("Token inválido: " + e.getMessage());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Token inválido");
                return;
            }

        } else {
            // Si falta el encabezado Authorization, devuelve 401
            System.out.println("Falta encabezado Authorization");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Falta Authorization");
            return;
        }

        // Si todo está bien, continúa con el flujo
        filterChain.doFilter(request, response);
    }
}
