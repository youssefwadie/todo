package com.github.youssefwadie.todo.user.confirmationtoken.dao;

import com.github.youssefwadie.todo.BaseRepositoryTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfirmationTokenRepositoryImplTest extends BaseRepositoryTests {
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Test
    void testInsertingConfirmationToken() {
//        final long userId = 1L;
//        final LocalDateTime createdAt = LocalDateTime.now();
//        final LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
//        final String token = RandomStringUtils.random(6, true, true);
//        final var confirmationToken = new ConfirmationToken(token, createdAt, expiresAt, userId);
//        confirmationTokenRepository.save(confirmationToken);
//
//        Optional<ConfirmationToken> confirmationInDB = confirmationTokenRepository.findById(1L);
//        assertThat(confirmationInDB.isPresent()).isTrue();
//
//        ConfirmationToken savedConfirmationToken = confirmationInDB.get();
//        assertThat(savedConfirmationToken.getUserId()).isNotNull();
//        assertThat(savedConfirmationToken.isConfirmed()).isFalse();
    }

}
