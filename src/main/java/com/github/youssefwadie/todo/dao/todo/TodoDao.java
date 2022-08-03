package com.github.youssefwadie.todo.dao.todo;

import com.github.youssefwadie.todo.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TodoDao {
    List<Todo>  findAllByUserId(Long userId);
    Todo save(Todo todo);
}
