package com.github.youssefwadie.todo.user.confirmationtoken.dao;

import com.github.youssefwadie.todo.BaseRepositoryTests;
import com.github.youssefwadie.todo.confirmationtoken.dao.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfirmationTokenRepositoryImplTest extends BaseRepositoryTests {
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Test
    void testInsertingConfirmationToken() {
//        final long userId = 1L;
//        final LocalDateTime createdAt = LocalDateTime.now();
//        final LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);
//        final String token = RandomStringUtils.random(6, true, true);
//        final var confirmationToken = new ConfirmationToken(token, createdAt, expiredAt, userId);
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
