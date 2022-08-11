package com.github.youssefwadie.todo.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private String headerName;
    private String key;
    private int accessTokenLifeTime;

    private SecretKey secretKey;

    private long refreshTokenLifeTime;

    private String authenticationScheme;
    private int refreshTokenCookieAge;

    private String refreshTokenCookieName;

    public TokenProperties() {
        this.headerName = "Authorization";
        this.key = "jxgE#*($#Qe_XH!uPq8Vdby@YFNkANd^u3dQ53YU%n4B";
        this.accessTokenLifeTime = 300_000; // 5 minutes (in ms)
        this.secretKey = null;
        this.refreshTokenLifeTime = 604_800_000L;  // one week (in ms)
        this.authenticationScheme = "Bearer";
        this.refreshTokenCookieAge = 604_800;     // one week (in s)
        this.refreshTokenCookieName = "refresh-token";
    }

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        try {
            refreshTokenCookieAge = Math.toIntExact(refreshTokenLifeTime / 1000);
        } catch (ArithmeticException ex) {
            refreshTokenCookieAge = Integer.MAX_VALUE;
        }
    }

}
