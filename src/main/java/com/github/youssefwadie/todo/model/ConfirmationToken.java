package com.github.youssefwadie.todo.model;

import lombok.*;

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
    private final LocalDateTime expiresAt;
    private final Long userId;

}
