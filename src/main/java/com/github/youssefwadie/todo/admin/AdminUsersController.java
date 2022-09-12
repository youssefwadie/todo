package com.github.youssefwadie.todo.admin;


import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.user.service.UserService;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class AdminUsersController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @DeleteMapping("/delete/{id:\\d+}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("id") Long userId) {
        if (!userService.existsById(userId)) return ResponseEntity.notFound().build();


        userService.deleteById(userId);
        SimpleResponseBody responseBody = new SimpleResponseBody
                .Builder(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase())
                .setMessage("user with id = %d deleted.".formatted(userId))
                .build();

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("{id:\\d+}/{enabled:true|false}")
    public ResponseEntity<Object> enableUser(@PathVariable("id") Long id, @PathVariable("enabled") boolean enabled) {
        userService.updateUserStatus(id, enabled);
        return ResponseEntity.ok().build();
    }
}