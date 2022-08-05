package com.github.youssefwadie.todo.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.youssefwadie.todo.dao.todo.TodoDao;
import com.github.youssefwadie.todo.dao.user.UserDao;
import com.github.youssefwadie.todo.model.User;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @MockBean
    UserDao userDao;

    @MockBean
    TodoDao todoDao;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        String email = "youssefwadie2@gmail.com";
        String password = "";
        User user = new User(userId, email, password);

        Mockito.doAnswer(invocation -> {
            Long passedUserId = invocation.getArgument(0);
            System.out.println("Deleting user id: " + passedUserId);
            assertThat(passedUserId).isEqualTo(userId);
            return null;
        }).when(todoDao).deleteAllByUserId(user.getId());
        
        Mockito.doAnswer(invocation -> {
        	User passedUser = invocation.getArgument(0);
        	System.out.printf("Deleting user ... %d - with email %s%n", passedUser.getId(), passedUser.getEmail());
        	return null;
        }).when(userDao).delete(user);
        
        userService.deleteUser(user);
        Mockito.verify(todoDao, Mockito.times(1)).deleteAllByUserId(userId);
        Mockito.verify(userDao, Mockito.times(1)).delete(user);
    }

    @Test
    void testFindUserById() {
    }
}
