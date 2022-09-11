package com.github.youssefwadie.todo.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.youssefwadie.todo.model.Role;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.exceptions.InvalidAuthenticationSchemeException;
import com.github.youssefwadie.todo.security.exceptions.InvalidJwtTokenTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.github.youssefwadie.todo.security.SecurityConstants.*;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final TokenProperties tokenProperties;

    public enum TOKEN_TYPE {
        ACCESS,
        REFRESH
    }

    public String extractAccessToken(String token) throws InvalidAuthenticationSchemeException {
        final String expectedAuthScheme = this.tokenProperties.getAuthenticationScheme();
        final String actualAuthScheme = extractAuthenticationScheme(token).orElseThrow(
                () -> new InvalidAuthenticationSchemeException("Invalid authentication scheme expected: %s, found none"
                        .formatted(this.tokenProperties.getAuthenticationScheme())));

        if (!expectedAuthScheme.equals(actualAuthScheme)) {
            throw new InvalidAuthenticationSchemeException("Invalid authentication scheme expected: %s, found %s"
                    .formatted(this.tokenProperties.getAuthenticationScheme(), actualAuthScheme));
        }

        return token.substring(expectedAuthScheme.length() + 1);
    }

    private static Optional<String> extractAuthenticationScheme(String token) {
        Assert.notNull(token, "token must not be null!");
        int spaceIndex = token.indexOf(' ');
        if (spaceIndex == -1) {
            return Optional.empty();
        }
        return Optional.of(token.substring(0, spaceIndex));
    }

    public String generatedAccessToken(Long userId, String email, List<String> simpleAuthorities) {
        Date now = new Date();
        return Jwts.builder()
                .setIssuer("Todo")
                .setSubject(email)
                .claim(USER_ID_CLAIM_NAME, userId)
                .claim(TOKEN_TYPE_CLAIM_NAME, TOKEN_TYPE_ACCESS_CLAIM_VALUE)
                .claim(TOKEN_USER_ROLES_CLAIM_NAME, simpleAuthorities)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenProperties.getAccessTokenLifeTime()))
                .signWith(tokenProperties.getSecretKey())
                .compact();
    }

    public List<String> getSimpleUserAuthorities(List<Role> userAuthorities) {
        if (userAuthorities == null) return Collections.emptyList();

        return userAuthorities.stream().map(Role::getName).toList();
    }

    public User parseUser(String jwt, TOKEN_TYPE expectedTokenType) throws InvalidJwtTokenTypeException {
        Assert.notNull(jwt, "jwt should not be null!");
        Claims claims = Jwts.parserBuilder().setSigningKey(this.tokenProperties.getSecretKey()).build().parseClaimsJws(jwt)
                .getBody();

        String tokenTypeClaim = claims.get(SecurityConstants.TOKEN_TYPE_CLAIM_NAME, String.class);
        if (tokenTypeClaim == null) {
            throw new InvalidJwtTokenTypeException(
                    "Expected token type to be one of %s".formatted(Arrays.toString(TOKEN_TYPE.values())));
        }
        try {
            TOKEN_TYPE actualTokenType = TOKEN_TYPE.valueOf(tokenTypeClaim);
            if (!actualTokenType.equals(expectedTokenType)) {
                throw new InvalidJwtTokenTypeException(
                        "Expected token type: %s, found: %s".formatted(expectedTokenType, actualTokenType));
            }


            Long id = claims.get(SecurityConstants.USER_ID_CLAIM_NAME, Long.class);
            String email = claims.getSubject();
            User user = new User();
            user.setId(id);
            user.setEmail(email);

            if (expectedTokenType.equals(TOKEN_TYPE.ACCESS)) {
                List<Object> authorities = (ArrayList<Object>) claims.get(TOKEN_USER_ROLES_CLAIM_NAME, ArrayList.class);
                List<Role> roles = authorities.stream().map(authority -> new Role(authority.toString())).toList();
                user.setRoles(roles);
            }

            return user;
        } catch (IllegalArgumentException ex) {
            throw new InvalidJwtTokenTypeException("Expected token type to be one of %s, found: %s"
                    .formatted(Arrays.toString(TOKEN_TYPE.values()), tokenTypeClaim));
        }

    }

    public LocalDateTime parseIssuedAt(String jwt) throws JsonProcessingException {
        int indexOfBodyStart = jwt.indexOf(".");
        if (indexOfBodyStart == -1) {
            throw new IllegalArgumentException("Invalid token header.");
        }
        int indexOfBodyEnd = jwt.indexOf(".", indexOfBodyStart + 1);
        if (indexOfBodyEnd == -1) {
            throw new IllegalArgumentException("Invalid token header.");
        }
        String encodedBody = jwt.substring(indexOfBodyStart + 1, indexOfBodyEnd);
        Base64.Decoder base64Decoder = Base64.getDecoder();
        String decodedBody = new String(base64Decoder.decode(encodedBody));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(decodedBody);
        JsonNode iatNode = node.get("iat");

        if (iatNode == null) {
            throw new IllegalArgumentException("Invalid token, iat claim is missing");
        }

        if (!iatNode.canConvertToLong()) {
            throw new IllegalArgumentException("Invalid claim, iat is not in seconds");
        }

        long iat = Long.parseLong(iatNode.toString());
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(iat), ZoneId.systemDefault());
    }
}
