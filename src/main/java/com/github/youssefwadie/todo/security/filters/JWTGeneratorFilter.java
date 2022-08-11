package com.github.youssefwadie.todo.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.youssefwadie.todo.config.TokenProperties;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import io.jsonwebtoken.Jwts;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.youssefwadie.todo.constants.SecurityConstants.*;

public class JWTGeneratorFilter extends OncePerRequestFilter {

    private final TokenProperties tokenProperties;

    public JWTGeneratorFilter(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

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
                    .setExpiration(new Date(now.getTime() + tokenProperties.getAccessTokenLifeTime()))
                    .signWith(tokenProperties.getSecretKey())
                    .compact();

            Date refreshTokenExpirationDate = new Date(now.getTime() + tokenProperties.getRefreshTokenLifeTime());
            String refreshToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(userDetails.getUsername())
                    .claim(USER_ID_CLAIM_NAME, userDetails.getUser().getId())
                    .claim(TOKEN_TYPE_CLAIM_NAME, TOKEN_TYPE_REFRESH_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(refreshTokenExpirationDate)
                    .signWith(tokenProperties.getSecretKey())
                    .compact();


            Map<String, String> accessAndRefreshTokens = new HashMap<>();
            accessAndRefreshTokens.put(JWT_ACCESS_TOKEN, accessToken);
            Cookie refreshTokenCookie = new Cookie(tokenProperties.getJwtRefreshTokenCookieName(), refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setMaxAge(tokenProperties.getRefreshTokenCookieAge());
            response.addCookie(refreshTokenCookie);


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
