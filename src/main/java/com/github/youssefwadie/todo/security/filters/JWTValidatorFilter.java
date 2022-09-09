package com.github.youssefwadie.todo.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.TokenProperties;
import com.github.youssefwadie.todo.security.util.JwtUtils;
import com.github.youssefwadie.todo.security.util.JwtUtils.TOKEN_TYPE;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.io.OutputStream;

@Slf4j
public class JWTValidatorFilter extends OncePerRequestFilter {

    private final TokenProperties tokenProperties;
    private final JwtUtils jwtUtils;

    public JWTValidatorFilter(TokenProperties tokenProperties, JwtUtils jwtUtils) {
        this.tokenProperties = tokenProperties;
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(tokenProperties.getAccessTokenHeaderNameSentByClient());
        if (jwt != null) {
            try {
                jwt = jwtUtils.extractAccessToken(jwt);
                User user = jwtUtils.parseUser(jwt, TOKEN_TYPE.ACCESS);
                TodoUserDetails userDetails = new TodoUserDetails(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } catch (Exception e) {
                log.error("Error while logging in: {}", e.getMessage());
                SimpleResponseBody simpleResponseBody = new SimpleResponseBody.Builder(HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase()).setMessage(e.getMessage()).build();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                OutputStream responseOutputStream = response.getOutputStream();
                new ObjectMapper().writeValue(responseOutputStream, simpleResponseBody);
                responseOutputStream.close();

                throw new BadCredentialsException(e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(tokenProperties.getAccessTokenHeaderNameSentByClient());
        String requestPath = request.getServletPath();
        if (authorizationHeader != null) {
            return !authorizationHeader.startsWith(tokenProperties.getAuthenticationScheme())
                    || requestPath.equals("/users/refresh");
        }

        return true;
    }
}
