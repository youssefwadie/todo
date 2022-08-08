package com.github.youssefwadie.todo.security.filters;

import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        if (jwt != null) {
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(SecurityConstants.key).build().parseClaimsJws(jwt).getBody();
                Long id = claims.get("user-id", Long.class);
                String email = claims.getSubject();
                User user = new User(id, email, "");

                TodoUserDetails userDetails = new TodoUserDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } catch (Exception e) {
                throw new BadCredentialsException(e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String authorizationHeader = request.getHeader(SecurityConstants.JWT_HEADER);
        if (authorizationHeader != null) {
            return authorizationHeader.startsWith("Basic");
        }
        return super.shouldNotFilter(request);
    }
}
