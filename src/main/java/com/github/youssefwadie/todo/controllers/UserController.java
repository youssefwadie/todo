package com.github.youssefwadie.todo.controllers;


import com.github.youssefwadie.todo.config.TokenProperties;
import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.util.JwtUtils;
import com.github.youssefwadie.todo.services.UserService;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final TokenProperties tokenProperties;
    private final JwtUtils jwtUtils;

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<User> login() {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(loggedInPrincipal.getUser());
    }


    @GetMapping(value = "/refresh", produces = "application/json")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request) {

        Cookie refreshTokenCookie = getAccessTokenCookie(request.getCookies());

        if (refreshTokenCookie == null) {
            SimpleResponseBody simpleResponseBody = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage("Expected %s header".formatted(tokenProperties.getHeaderName()))
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleResponseBody);
        }

        try {
            String authorizationHeader = refreshTokenCookie.getValue();

            User user = jwtUtils.parseUser(authorizationHeader, JwtUtils.TOKEN_TYPE.REFRESH);
            Date now = new Date();
            String accessToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(user.getEmail())
                    .claim(SecurityConstants.USER_ID_CLAIM_NAME, user.getId())
                    .claim(SecurityConstants.TOKEN_TYPE_CLAIM_NAME, SecurityConstants.TOKEN_TYPE_ACCESS_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + tokenProperties.getAccessTokenLifeTime()))
                    .signWith(tokenProperties.getSecretKey())
                    .compact();

            SimpleResponseBody token = new SimpleResponseBody
                    .Builder(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase())
                    .setMessage("access token is refreshed, can be found in header: %s, `%s access-token`".formatted(tokenProperties.getHeaderName(), tokenProperties.getAuthenticationScheme()))
                    .build();

            return ResponseEntity.status(HttpStatus.OK).header(tokenProperties.getHeaderName(),
                    tokenProperties.getAuthenticationScheme() + " " + accessToken).body(token);

        } catch (Exception ex) {
            SimpleResponseBody error = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage(ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = service.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConstraintsViolationException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getErrors());
        }
    }

    private Cookie getAccessTokenCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenProperties.getRefreshTokenCookieName())) return cookie;
        }
        return null;
    }
}
