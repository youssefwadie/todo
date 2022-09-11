package com.github.youssefwadie.todo.security;


import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TodoUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public TodoUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userByEmail = repository.findByEmail(email);
        if (userByEmail.isEmpty()) {
            log.error("No user name with email: {} was founded.", email);
            throw new UsernameNotFoundException("No user name with email: %s was founded.".formatted(email));
        }
        log.info("{} is trying to login.", email);
        return new TodoUserDetails(userByEmail.get());
    }
}
