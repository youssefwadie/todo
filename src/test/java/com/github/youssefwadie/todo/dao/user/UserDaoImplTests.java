package com.github.youssefwadie.todo.dao.user;

import com.github.youssefwadie.todo.dao.BaseDaoTests;
import com.github.youssefwadie.todo.dao.role.RoleDao;
import com.github.youssefwadie.todo.model.Role;
import com.github.youssefwadie.todo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class UserDaoImplTests extends BaseDaoTests {
    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

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
    void testFindUserById() {
        Long id = 1L;
        Optional<User> userOptional = userDao.findById(id);
        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getRoles().size()).isEqualTo(2);
    }

    @Test
    void testUpdatingUserRoles() {
        Long userId = 1L;
        User user = new User();
        user.setEmail("youssef@mail.com");
        user.setPassword("123456789");
        user.setId(userId);
        List<Role> userRoles = List.of(new Role(1L, null, null));
        user.setRoles(userRoles);

        User savedUser = userDao.save(user);
        assertThat(savedUser.getRoles().size()).isEqualTo(1);
    }

    @Test
    void testAddingAndDeletingUserRole() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("youssef@mail.com");
        user.setPassword("123456789");
        List<Role> userRoles = List.of(new Role(1L, null, null), new Role(null, "New Role", null));
        user.setRoles(userRoles);

        User savedUser = userDao.save(user);
        assertThat(savedUser.getRoles()).isNotNull();
        // one deleted and one added
        assertThat(savedUser.getRoles().size()).isEqualTo(2);
        assertThat(savedUser.getRoles().get(1).getId()).isNotNull();
    }

    @Test
    void testListAll() {
        Iterable<User> users = userDao.findAll();
        assertThat(users).isNotNull();
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        userDao.deleteById(user.getId());
        List<Role> usersRoles = roleDao.findAllByUserId(userId);
        assertThat(usersRoles.isEmpty()).isTrue();
    }

}

