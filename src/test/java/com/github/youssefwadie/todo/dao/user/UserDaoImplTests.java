package com.github.youssefwadie.todo.dao.user;

import com.github.youssefwadie.todo.dao.BaseDaoTests;
import com.github.youssefwadie.todo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class UserDaoImplTests extends BaseDaoTests {
    @Autowired
    UserDao userDao;

    @Test
    void testUpdatingUser() {
        Long id = 1L;
        String email = "admin@mail.com";
        String password = "123456789";
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        User savedUser = userDao.save(user);


        assertThat(savedUser.getId()).isEqualTo(id);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void testListAll() {
        Iterable<User> users = userDao.findAll();
        assertThat(users).isNotNull();

    }

}

