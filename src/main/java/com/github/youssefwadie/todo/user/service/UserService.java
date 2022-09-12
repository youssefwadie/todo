package com.github.youssefwadie.todo.user.service;

import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.security.exceptions.InvalidPasswordException;
import com.github.youssefwadie.todo.security.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.todoitem.TodoItemService;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import com.github.youssefwadie.todo.user.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TodoItemService todoItemService;
    private final RoleRepository roleRepository;

    private final UserValidatorService userValidator;
    private final PasswordEncoder passwordEncoder;

    public User addUser(User user) throws ConstraintsViolationException {
        userValidator.validateUser(user);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

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
        save(databaseUser);
    }

    public User save(User user) throws ConstraintsViolationException {
        userValidator.validateUser(user);
        return userRepository.save(user);
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
        roleRepository.deleteAllUsersRolesById(userId);
        todoItemService.deleteAllByUserId(userId);
        userRepository.deleteById(userId);
    }

    public void updateUserStatus(Long id, boolean enabled) {
        userRepository.updateUserStatus(id, enabled);
    }
}
