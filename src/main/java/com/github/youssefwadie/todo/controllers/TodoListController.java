package com.github.youssefwadie.todo.controllers;

import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.Todo;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.services.TodoService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1/todo", produces = "application/json")
public class TodoListController {

    private final TodoService todoService;

    public TodoListController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping(value = "list", produces = "application/json")
    public ResponseEntity<List<Todo>> list() {
        User loggedUser = getLoggedUser();
        return ResponseEntity.ok(todoService.findAll(loggedUser.getId()));
    }

    private User getLoggedUser() {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loggedInPrincipal.getUser();
    }

    @PostMapping(value = "/create", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createTodo(@RequestBody Todo todo) throws URISyntaxException, ConstraintViolationException {
        try {
            User loggedUser = getLoggedUser();

            todo.setUserId(loggedUser.getId());
            Todo savedTodo = todoService.save(todo);

            URI saveTodoURI = new URI("/api/v1/todo/" + savedTodo.getId());

            return ResponseEntity.created(saveTodoURI).body(savedTodo);
        } catch (ConstraintsViolationException ex) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ex.getErrors());
        }
    }
}
