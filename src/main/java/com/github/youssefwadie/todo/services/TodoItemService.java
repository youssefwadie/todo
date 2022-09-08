package com.github.youssefwadie.todo.services;

import com.github.youssefwadie.todo.dao.todo.TodoItemDao;
import com.github.youssefwadie.todo.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.model.TodoItem;
import com.github.youssefwadie.todo.security.util.BasicValidator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TodoItemService {
    private final TodoItemDao todoItemDao;

    private final UserService userService;

    public TodoItemService(TodoItemDao todoItemDao, UserService userService) {
        this.todoItemDao = todoItemDao;
        this.userService = userService;
    }

    public List<TodoItem> findAll(Long userId) {
        return (List<TodoItem>) todoItemDao.findAllByUserId(userId);
    }

    public TodoItem save(TodoItem todoItem) throws ConstraintsViolationException {
        validateTodo(todoItem, true, true);
        return todoItemDao.save(todoItem);
    }

    public TodoItem update(TodoItem todoItem) throws ConstraintsViolationException {
        validateTodo(todoItem, false, true);
        return todoItemDao.save(todoItem);
    }

    public Optional<TodoItem> findById(Long id) {
        return todoItemDao.findById(id);
    }

    public boolean notOwnedByUser(Long id, Long userId) {
        return !todoItemDao.ownedByUser(id, userId);
    }

    private void validateTodo(TodoItem todo, boolean checkDate, boolean newItem) throws ConstraintsViolationException {
        Map<String, String> errors = new HashMap<>();
        if (newItem) {
            todo.setId(null);
        }

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

        if (checkDate && BasicValidator.isInThePast(todo.getDeadTime())) {
            errors.put("deadTime", "deadTime cannot be in the past or empty");
        }

        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }

    public void setDone(Long id, Boolean done) {
        todoItemDao.setDone(id, done);
    }
}
