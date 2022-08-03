package com.github.youssefwadie.todo.services;

import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.Todo;
import com.github.youssefwadie.todo.repositories.TodoRepository;
import com.github.youssefwadie.todo.util.BasicValidator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    private final UserService userService;

    public TodoService(TodoRepository todoRepository, UserService userService) {
        this.todoRepository = todoRepository;
        this.userService = userService;
    }

    public List<Todo> findAll(Long userId) {
        return todoRepository.findAllByUserId(userId);
    }

    public Todo save(Todo todo) throws ConstraintsViolationException {
        validateTodo(todo);
        return todoRepository.save(todo);
    }

    public void validateTodo(Todo todo) throws ConstraintsViolationException{
        Map<String, String> errors = new HashMap<>();
        if (BasicValidator.isBlank(todo.getTitle())) {
            errors.put("title", "Cannot be blank or null");
        }

        if (BasicValidator.isBlank(todo.getDescription())) {
            errors.put("description", "Cannot be blank or null");
        }

        Long userId = todo.getUserId();
        if (Objects.isNull(userId)) {
            errors.put("userId", "Cannot be null");
        } else {
            boolean userExists = userService.existsById(userId);
            if (!userExists) {
                errors.put("userId", "No user with id: %d was found.".formatted(userId));
            }
        }

        if (BasicValidator.isInThePast(todo.getDeadTime())) {
            errors.put("deadTime", "deadTime cannot be in the past or empty");
        }


        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }
}
