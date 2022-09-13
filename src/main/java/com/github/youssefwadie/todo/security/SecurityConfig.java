package com.github.youssefwadie.todo.security;

import com.github.youssefwadie.todo.security.filters.JWTGeneratorFilter;
import com.github.youssefwadie.todo.security.filters.JWTValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Configuration
//@EnableWebSecurity(debug = true)
@EnableConfigurationProperties(TokenProperties.class)
public class SecurityConfig {

    private final TokenProperties tokenProperties;
    private final JwtService jwtService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Collections.singletonList("*"));
            configuration.setAllowedMethods(Collections.singletonList("*"));
            configuration.setExposedHeaders(List.of("Authorization", "X-Access-Token"));
            configuration.setMaxAge(3600L);
            return configuration;
        });

        http.csrf().disable();

        http.addFilterBefore(jwtValidatorFilter(), BasicAuthenticationFilter.class);
        http.addFilterAfter(jwtGeneratorFilter(), BasicAuthenticationFilter.class);
        http.authorizeRequests();

        http.authorizeRequests(request -> {
            request.antMatchers("/admin/**").hasAuthority("Admin");
            request.antMatchers(HttpMethod.GET, "/users/refresh").permitAll();
            request.antMatchers(HttpMethod.POST, "/users").permitAll();
            request.antMatchers("/registration/**").permitAll();
            request.anyRequest().authenticated();
        });

        // http.formLogin();
        http.httpBasic();
        return http.build();
    }

    JWTValidatorFilter jwtValidatorFilter() {
        return new JWTValidatorFilter(tokenProperties, jwtService);
    }

    JWTGeneratorFilter jwtGeneratorFilter() {
        return new JWTGeneratorFilter(tokenProperties, jwtService);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
