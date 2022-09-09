package com.github.youssefwadie.todo.security.filters;

import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.TokenProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

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


            ResponseCookie cookie = ResponseCookie.from(tokenProperties.getRefreshTokenCookieName(), refreshToken)
                    .sameSite(Cookie.SameSite.LAX.attributeValue())
                    .secure(true)
                    .httpOnly(true)
                    .maxAge(tokenProperties.getRefreshTokenCookieAge())
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            response.setHeader(tokenProperties.getAccessTokenHeaderNameGeneratedByServer(), accessToken);
        }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/users/login");
    }
}
