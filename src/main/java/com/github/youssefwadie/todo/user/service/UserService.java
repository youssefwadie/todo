package com.github.youssefwadie.todo.user.service;

import com.github.youssefwadie.todo.model.ConfirmationToken;
import com.github.youssefwadie.todo.model.Role;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.security.exceptions.InvalidPasswordException;
import com.github.youssefwadie.todo.security.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.todoitem.TodoItemService;
import com.github.youssefwadie.todo.confirmationtoken.ConfirmationTokenService;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import com.github.youssefwadie.todo.user.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String DEFAULT_USER_ROLE = "User";
    private static final int EXPIRES_AFTER_MINUTES = 15;

    private final UserRepository userRepository;
    private final TodoItemService todoItemService;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserValidatorService userValidator;
    private final PasswordEncoder passwordEncoder;


    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    @Modifying
    public void deleteUser(User user) {
        roleRepository.deleteAllUsersRolesById(user.getId());
        todoItemService.deleteAllByUserId(user.getId());
        userRepository.delete(user);
    }

    public User findById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No User with id: %d was found!".formatted(userId)));
    }

    public void changePassword(String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException, ConstraintsViolationException {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = loggedInPrincipal.getUser();
        User databaseUser = findById(loggedInUser.getId());
        if (!passwordEncoder.matches(oldPassword, databaseUser.getPassword())) {
            throw new InvalidPasswordException(InvalidPasswordException.PASSWORD_TYPE.OLD, "old password doesn't match the stored password");
        }
        boolean validPassword = userValidator.isValidPassword(newPassword);
        if (!validPassword) {
            throw new InvalidPasswordException(InvalidPasswordException.PASSWORD_TYPE.NEW, UserValidatorService.PASSWORD_VALIDATION_MESSAGE);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        databaseUser.setPassword(encodedPassword);
        this.save(databaseUser);
    }

    @Transactional
    public User save(User user) throws ConstraintsViolationException {
        userValidator.validateUser(user);

        if (user.getId() == null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        return userRepository.save(user);
    }

    /**
     * Register a user registration request
     *
     * @param user the new user details
     * @return the confirmation token for the user email
     * @throws ConstraintsViolationException if the given user's email or password is invalid
     */
    @Transactional
    public String singUpUser(User user) throws ConstraintsViolationException {
        Optional<Role> defaultRole = roleRepository.findByName(DEFAULT_USER_ROLE);
        defaultRole.ifPresent(role -> user.setRoles(Collections.singletonList(role)));

        final User savedUser = this.save(user);
        final String token = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();
        ConfirmationToken confirmationToken = new ConfirmationToken(token,
                now,
                now.plusMinutes(EXPIRES_AFTER_MINUTES),
                savedUser.getId());

        ConfirmationToken savedConfirmationToken = confirmationTokenService.save(confirmationToken);

        // TODO: send confirmation mail
        // TODO: build confirmation url
        return savedConfirmationToken.getToken();
    }


    public User findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No User with email: %s was found!".formatted(email)));
    }


    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional
    @Modifying
    public void deleteById(Long userId) {
        confirmationTokenService.deleteAllByUserId(userId);
        roleRepository.deleteAllUsersRolesById(userId);
        todoItemService.deleteAllByUserId(userId);
        userRepository.deleteById(userId);
    }

    public void updateUserStatus(Long id, boolean enabled) {
        userRepository.updateUserStatus(id, enabled);
    }

    public void enableUserById(Long id) {
        userRepository.updateUserStatus(id, true);
    }
}
