package com.github.youssefwadie.todo.constants;

import com.github.youssefwadie.todo.security.util.JwtUtils.TOKEN_TYPE;

public final class SecurityConstants {

    private SecurityConstants() {
    }


    public static final String USER_ID_CLAIM_NAME = "client_id";
    public static final String TOKEN_TYPE_CLAIM_NAME = "token_type";
    public static final String TOKEN_TYPE_ACCESS_CLAIM_VALUE = TOKEN_TYPE.ACCESS.toString();
    public static final String TOKEN_TYPE_REFRESH_CLAIM_VALUE = TOKEN_TYPE.REFRESH.toString();

}
