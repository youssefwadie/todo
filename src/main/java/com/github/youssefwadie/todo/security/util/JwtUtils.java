package com.github.youssefwadie.todo.security.util;

import com.github.youssefwadie.todo.config.TokenProperties;
import com.github.youssefwadie.todo.constants.SecurityConstants;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.exceptions.InvalidAuthenticationSchemeException;
import com.github.youssefwadie.todo.security.exceptions.InvalidJwtTokenTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Optional;

@Component
@AllArgsConstructor
public class JwtUtils {

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

    public User parseUser(String jwt, TOKEN_TYPE type) throws InvalidJwtTokenTypeException {
        Assert.notNull(jwt, "jwt should not be null!");
        Claims claims = Jwts.parserBuilder().setSigningKey(this.tokenProperties.getSecretKey()).build().parseClaimsJws(jwt)
                .getBody();

        String tokenTypeClaim = claims.get(SecurityConstants.TOKEN_TYPE_CLAIM_NAME, String.class);
        if (tokenTypeClaim == null) {
            throw new InvalidJwtTokenTypeException(
                    "Expected token type to be one of %s".formatted(Arrays.toString(TOKEN_TYPE.values())));
        }
        try {
            TOKEN_TYPE tokenType = TOKEN_TYPE.valueOf(tokenTypeClaim);
            if (!tokenType.equals(type)) {
                throw new InvalidJwtTokenTypeException(
                        "Expected token type: %s, found: %s".formatted(type, tokenType));
            }
            Long id = claims.get(SecurityConstants.USER_ID_CLAIM_NAME, Long.class);
            String email = claims.getSubject();
            return new User(id, email, "");
        } catch (IllegalArgumentException ex) {
            throw new InvalidJwtTokenTypeException("Expected token type to be one of %s, found: %s"
                    .formatted(Arrays.toString(TOKEN_TYPE.values()), tokenTypeClaim));
        }

    }
}
