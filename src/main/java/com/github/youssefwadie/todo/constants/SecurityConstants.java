package com.github.youssefwadie.todo.constants;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import com.github.youssefwadie.todo.security.util.JwtUtils.TOKEN_TYPE;

import io.jsonwebtoken.security.Keys;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String JWT_KEY = "jxgE#*($#Qe_XH!uPq8Vdby@YFNkANd^u3dQ53YU%n4B";
    public static final byte[] JWT_TOKEN_BYTES = JWT_KEY.getBytes(StandardCharsets.UTF_8);
    public static final int ACCESS_TOKEN_LIFE_TIME = 90_000; // one and a half minute
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SecurityConstants.JWT_TOKEN_BYTES);


    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_REFRESH_TOKEN = "refresh_token";
    public static final String JWT_ACCESS_TOKEN = "access_token";

    public static final int REFRESH_TOKEN_LIFE_TIME = 900_000; // 15 minutes

    public static final String JWT_AUTHENTICATION_SCHEME = "Bearer";
    
    public static final String USER_ID_CLAIM_NAME = "client_id";
    public static final String TOKEN_TYPE_CLAIM_NAME = "token_type";
    public static final String TOKEN_TYPE_ACCESS_CLAIM_VALUE = TOKEN_TYPE.ACCESS.toString();
    public static final String TOKEN_TYPE_REFRESH_CLAIM_VALUE = TOKEN_TYPE.REFRESH.toString();
    
}
