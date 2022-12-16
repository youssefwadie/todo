package com.github.youssefwadie.todo.confirmationtoken;

import com.github.youssefwadie.todo.confirmationtoken.dao.ConfirmationTokenRepository;
import com.github.youssefwadie.todo.model.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository repository;

    public ConfirmationToken save(ConfirmationToken token) {
        return repository.save(token);
    }


    public Optional<ConfirmationToken> getToken(String token) {
        return repository.findByToken(token);
    }

    public void deleteAllByUserId(Long userId) {
        repository.deleteAllByUserId(userId);
    }
}
