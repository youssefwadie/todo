package com.github.youssefwadie.todo.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import com.github.youssefwadie.todo.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.youssefwadie.todo.todoitem.dao.TodoItemRepository;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import com.github.youssefwadie.todo.security.exceptions.UserNotFoundException;
import com.github.youssefwadie.todo.model.User;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @MockBean
    UserRepository userRepository;

    @MockBean
    TodoItemRepository todoDao;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        String email = "youssefwadie2@gmail.com";
        String password = "";
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPassword(password);

        Mockito.doAnswer(invocation -> {
            Long passedUserId = invocation.getArgument(0);
            System.out.println("Deleting user id: " + passedUserId);
            assertThat(passedUserId).isEqualTo(userId);
            return null;
        }).when(todoDao).deleteAllByUserId(user.getId());

        Mockito.doAnswer(invocation -> {
            User passedUser = invocation.getArgument(0);
            System.out.printf("Deleting user ... %d - with email %s%n", passedUser.getId(), passedUser.getEmail());
            assertThat(passedUser.getId()).isEqualTo(userId);
            return null;
        }).when(userRepository).delete(user);

        userService.deleteUser(user);
        Mockito.verify(todoDao, Mockito.times(1)).deleteAllByUserId(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    void testFindUserByIdThrowsException() throws UserNotFoundException {
        Long userId = 84568L;
        Optional<User> userOptional = Optional.empty();
        Mockito.when(userRepository.findById(userId)).thenReturn(userOptional);

        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(userId);
        });

    }


    @Test
    void testFindUserById() throws UserNotFoundException {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findById(userId)).thenReturn(userOptional);
        User retrievedUser = userService.findById(userId);
        System.out.println(retrievedUser);
        assertThat(retrievedUser.getId()).isEqualTo(user.getId());
    }
}
