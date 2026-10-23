package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. Разрешаем запросы с вашего фронтенда (для разработки — все, для прода — конкретный домен)
        config.setAllowedOriginPatterns(List.of("*"));
        // Для production лучше: config.setAllowedOrigins(List.of("https://myapp.com"));

        // 2. Разрешаем все HTTP-методы
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 3. Разрешаем нужные заголовки (Content-Type для JSON, Authorization для JWT)
        config.setAllowedHeaders(List.of("*"));

        // 4. Разрешаем браузеру читать заголовки в ответе
        config.setExposedHeaders(List.of("Authorization"));

        // 5. Разрешаем передачу куки и заголовка Authorization
        config.setAllowCredentials(true);

        // 6. Кэшируем результат preflight-запроса (OPTIONS) на 1 час
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}