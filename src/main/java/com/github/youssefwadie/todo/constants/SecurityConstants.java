package com.github.youssefwadie.todo.constants;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public final class SecurityConstants {
    private SecurityConstants() {}

    public static final String JWT_KEY = "jxgE#*($#Qe_XH!uPq8Vdby@YFNkANd^u3dQ53YU%n4B";
    public static final byte[] JWT_TOKEN_BYTES = JWT_KEY.getBytes(StandardCharsets.UTF_8);
    public static final long JWT_LIFE_TIME = 3000_000; // 5 minutes

    public static final String JWT_AUTHENTICATION_SCHEME = "Bearer ";
    public static final String JWT_HEADER = "Authorization";

    public static final SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_TOKEN_BYTES);
}
