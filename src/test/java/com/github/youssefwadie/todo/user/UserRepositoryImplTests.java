package com.github.youssefwadie.todo.user;

import com.github.youssefwadie.todo.BaseRepositoryTests;
import com.github.youssefwadie.todo.user.dao.UserRepository;
import com.github.youssefwadie.todo.user.role.RoleRepository;
import com.github.youssefwadie.todo.model.Role;
import com.github.youssefwadie.todo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class UserRepositoryImplTests extends BaseRepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testUpdatingUser() {
        Long id = 1L;
        String email = "admin@mail.com";
        String password = "123456789";
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        User savedUser = userRepository.save(user);


        assertThat(savedUser.getId()).isEqualTo(id);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindUserById() {
        Long id = 1L;
        Optional<User> userOptional = userRepository.findById(id);
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

        User savedUser = userRepository.save(user);
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

        User savedUser = userRepository.save(user);
        assertThat(savedUser.getRoles()).isNotNull();
        // one deleted and one added
        assertThat(savedUser.getRoles().size()).isEqualTo(2);
        assertThat(savedUser.getRoles().get(1).getId()).isNotNull();
    }

    @Test
    void testListAll() {
        Iterable<User> users = userRepository.findAll();
        assertThat(users).isNotNull();
    }

    @Test
    void testDeleteUser() {
        Long userRolesCountBeforeDeleting = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users_roles", Long.class);;
        Long rolesCountBeforeDeleting = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles", Long.class);

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        userRepository.deleteById(user.getId());

        Long userRolesCountAfterDeleting = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users_roles", Long.class);
        Long rolesCountAfterDeleting = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles", Long.class);

        List<Role> usersRoles = roleRepository.findAllByUserId(userId);

        assertThat(usersRoles.isEmpty()).isTrue();
        assertThat(userRolesCountBeforeDeleting).isGreaterThan(userRolesCountAfterDeleting);
        assertThat(rolesCountBeforeDeleting).isEqualTo(rolesCountAfterDeleting);
    }

}

