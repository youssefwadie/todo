package com.github.youssefwadie.todo.todoitem;

import com.github.youssefwadie.todo.model.TodoItem;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.todoitem.dao.TodoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TodoItemService {
    private final TodoItemRepository todoItemRepository;
    private final TodoItemValidatorService validatorService;


    public List<TodoItem> findAllByUserId(Long userId) {
        return (List<TodoItem>) todoItemRepository.findAllByUserId(userId);
    }

    public TodoItem save(TodoItem todoItem) throws ConstraintsViolationException {
        validatorService.validateTodo(todoItem, todoItem.getId() == null);
        return todoItemRepository.save(todoItem);
    }


    public Optional<TodoItem> findById(Long id) {
        return todoItemRepository.findById(id);
    }

    public boolean notOwnedByUser(Long id, Long userId) {
        return !todoItemRepository.ownedByUser(id, userId);
    }


    public void setDone(Long id, Boolean done) {
        todoItemRepository.setDone(id, done);
    }
}
