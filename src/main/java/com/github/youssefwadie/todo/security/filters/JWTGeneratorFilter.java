package com.github.youssefwadie.todo.security.filters;

import static com.github.youssefwadie.todo.constants.SecurityConstants.ACCESS_TOKEN_LIFE_TIME;
import static com.github.youssefwadie.todo.constants.SecurityConstants.JWT_ACCESS_TOKEN;
import static com.github.youssefwadie.todo.constants.SecurityConstants.JWT_REFRESH_TOKEN;
import static com.github.youssefwadie.todo.constants.SecurityConstants.REFRESH_TOKEN_LIFE_TIME;
import static com.github.youssefwadie.todo.constants.SecurityConstants.SECRET_KEY;
import static com.github.youssefwadie.todo.constants.SecurityConstants.TOKEN_TYPE_ACCESS_CLAIM_VALUE;
import static com.github.youssefwadie.todo.constants.SecurityConstants.TOKEN_TYPE_CLAIM_NAME;
import static com.github.youssefwadie.todo.constants.SecurityConstants.TOKEN_TYPE_REFRESH_CLAIM_VALUE;
import static com.github.youssefwadie.todo.constants.SecurityConstants.USER_ID_CLAIM_NAME;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.youssefwadie.todo.security.TodoUserDetails;

import io.jsonwebtoken.Jwts;

public class JWTGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            TodoUserDetails userDetails = (TodoUserDetails) (authentication.getPrincipal());
            Date now = new Date();
            String accessToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(userDetails.getUsername())
                    .claim(USER_ID_CLAIM_NAME, userDetails.getUser().getId())
                    .claim(TOKEN_TYPE_CLAIM_NAME, TOKEN_TYPE_ACCESS_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_LIFE_TIME))
                    .signWith(SECRET_KEY)
                    .compact();


            String refreshToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(userDetails.getUsername())
                    .claim(USER_ID_CLAIM_NAME, userDetails.getUser().getId())
                    .claim(TOKEN_TYPE_CLAIM_NAME, TOKEN_TYPE_REFRESH_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_LIFE_TIME))
                    .signWith(SECRET_KEY)
                    .compact();


            Map<String, String> accessAndRefreshTokens = new HashMap<>();
            accessAndRefreshTokens.put(JWT_ACCESS_TOKEN, accessToken);
            accessAndRefreshTokens.put(JWT_REFRESH_TOKEN, refreshToken);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            OutputStream responseOutputStream = response.getOutputStream();
            new ObjectMapper().writeValue(responseOutputStream, accessAndRefreshTokens);
            responseOutputStream.close();
        }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/users/login");
    }
}
