package com.ayuntamiento.gateway.config;

import com.ayuntamiento.gateway.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;

@Configuration
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> loggingFilter() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthFilter());
        registrationBean.addUrlPatterns("/*"); // ✅ Aplica a todas las rutas
        registrationBean.setOrder(1); // Prioridad alta si tienes más filtros

        return registrationBean;
    }
}
