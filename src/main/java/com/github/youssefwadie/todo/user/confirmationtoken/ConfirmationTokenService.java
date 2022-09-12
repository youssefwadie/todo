package com.github.youssefwadie.todo.user.confirmationtoken;

import com.github.youssefwadie.todo.model.ConfirmationToken;
import com.github.youssefwadie.todo.user.confirmationtoken.dao.ConfirmationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {
    private static final int CONFIRMATION_TOKEN_LENGTH = 6;
    private static final int EXPIRES_AFTER_MINUTES = 15;

    private final ConfirmationTokenRepository repository;

    @Transactional
    public void addConfirmationTokenForUser(Long userId) {
        final String token = RandomStringUtils.random(CONFIRMATION_TOKEN_LENGTH, true, true);
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(EXPIRES_AFTER_MINUTES), userId);

        repository.save(confirmationToken);
    }


}
