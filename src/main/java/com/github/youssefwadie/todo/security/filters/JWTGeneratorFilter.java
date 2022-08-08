package com.github.youssefwadie.todo.security.filters;

import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JWTGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            TodoUserDetails userDetails = (TodoUserDetails) (authentication.getPrincipal());
            Date now = new Date();
            String jwt = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(userDetails.getUsername())
                    .claim("user-id", userDetails.getUser().getId())
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + SecurityConstants.JWT_LIFE_TIME)).signWith(SecurityConstants.key)
                    .compact();

            response.setHeader(SecurityConstants.JWT_HEADER, String.format("%s %s", SecurityConstants.JWT_AUTHENTICATION_SCHEME, jwt));

        }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/users/login");
    }
}
