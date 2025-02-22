package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // Create and configure the custom authentication filter
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager);
        // Set custom login endpoint
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/user/login");

        http
                .csrf(AbstractHttpConfigurer::disable)
                // Disable anonymous authentication using the lambda syntax
                .anonymous(anonymous -> anonymous.disable())
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll();
                    registry.requestMatchers(HttpMethod.POST, "/api/user/login").permitAll();
                    registry.anyRequest().authenticated();
                })
                // Register the custom DAO authentication provider
                .authenticationProvider(authenticationProvider())
                .addFilter(jwtAuthenticationFilter)
                // Register the verification filter (ensure it’s not annotated with @Component to avoid duplicate registration)
                .addFilterAfter(new JWTAuthenticationVerificationFilter(authenticationManager), JWTAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }
}
