package com.github.youssefwadie.todo.controllers;


import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.TokenProperties;
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
import java.time.LocalDateTime;
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


    @GetMapping(value = "/refresh")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request) {

        Cookie refreshTokenCookie = getAccessTokenCookie(request.getCookies());

        if (refreshTokenCookie == null) {
            return unauthorizedResponse("Expected %s cookie".formatted(tokenProperties.getRefreshTokenCookieName()));
        }


        String jwt = refreshTokenCookie.getValue();
        try {
            LocalDateTime issueAt = jwtUtils.getIssueAt(jwt);
            User parsedUser = jwtUtils.parseUser(jwt, JwtUtils.TOKEN_TYPE.REFRESH);
            User userInDB = service.findById(parsedUser.getId());
            if (userInDB.getUpdatedAt().isAfter(issueAt)) {
                return unauthorizedResponse("user details has been changed, please re-login.");
            }

            Date now = new Date();
            String accessToken = Jwts.builder()
                    .setIssuer("Todo")
                    .setSubject(userInDB.getEmail())
                    .claim(SecurityConstants.USER_ID_CLAIM_NAME, userInDB.getId())
                    .claim(SecurityConstants.TOKEN_TYPE_CLAIM_NAME, SecurityConstants.TOKEN_TYPE_ACCESS_CLAIM_VALUE)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + tokenProperties.getAccessTokenLifeTime()))
                    .signWith(tokenProperties.getSecretKey())
                    .compact();

            SimpleResponseBody token = new SimpleResponseBody
                    .Builder(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase())
                    .setMessage("access token is refreshed, can be found in header: %s`".formatted(tokenProperties.getAccessTokenHeaderNameGeneratedByServer()))
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(tokenProperties.getAccessTokenHeaderNameGeneratedByServer(), accessToken)
                    .body(token);
        } catch (Exception ex) {
            SimpleResponseBody error = new SimpleResponseBody
                    .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .setMessage(ex.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = service.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ConstraintsViolationException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(e.getErrors());
        }
    }

    private ResponseEntity<Object> unauthorizedResponse(String message) {
        SimpleResponseBody simpleResponseBody = new SimpleResponseBody
                .Builder(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .setMessage(message)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleResponseBody);
    }

    private Cookie getAccessTokenCookie(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(tokenProperties.getRefreshTokenCookieName())) return cookie;
        }
        return null;
    }
}
