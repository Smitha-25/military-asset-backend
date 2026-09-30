
package com.kodnest.app.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
        		                                                   "http://localhost:5173",
        		                                                   "http://localhost:5176"));
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        config.setAllowedHeaders(
            List.of("Authorization", "Content-Type")
        );
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth

                .requestMatchers(HttpMethod.GET, "/api/bases/**",
                                 "/api/equipment/**")
                    .hasAnyRole("ADMIN", "LOGISTICS_OFFICER", "BASE_COMMANDER")

                .requestMatchers("/api/bases/**",
                                 "/api/equipment/**",
                                 "/api/users/**",
                                 "/api/audit-logs/**")
                    .hasRole("ADMIN")

                .requestMatchers("/api/purchases/**",
                                 "/api/transfers/**")
                    .hasAnyRole("ADMIN", "LOGISTICS_OFFICER")

                .requestMatchers("/api/stock/**",
                                 "/api/assignments/**",
                                 "/api/expenditures/**",
                                 "/api/dashboard/**")
                    .hasAnyRole("ADMIN", "BASE_COMMANDER")

                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});

        return http.build();
    }
}