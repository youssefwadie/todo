package com.github.youssefwadie.todo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ConfirmationToken {
    private Long id;
    private final String token;
    private boolean confirmed;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiredAt;
    private final Long userId;

}
