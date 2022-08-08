package com.github.youssefwadie.todo.security;


import com.github.youssefwadie.todo.repositories.UserRepository;
import com.github.youssefwadie.todo.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public TodoUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userByEmail = repository.findByEmail(email);
        if (userByEmail.isEmpty()) {
            throw new UsernameNotFoundException("No user name with email: %s was founded.".formatted(email));
        }
        return new TodoUserDetails(userByEmail.get());
    }
}
