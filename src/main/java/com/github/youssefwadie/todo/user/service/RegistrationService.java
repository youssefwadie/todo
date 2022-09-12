package com.github.youssefwadie.todo.user.service;

import com.github.youssefwadie.todo.model.Role;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.model.UserRegistrationRequest;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.user.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private static final String DEFAULT_USER_ROLE = "User";

    public User addUser(UserRegistrationRequest registrationRequest) throws ConstraintsViolationException {
        final User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());
        Optional<Role> defaultRole = roleRepository.findByName(DEFAULT_USER_ROLE);
        defaultRole.ifPresent(role -> user.setRoles(Collections.singletonList(role)));
        return userService.addUser(user);
    }
}
