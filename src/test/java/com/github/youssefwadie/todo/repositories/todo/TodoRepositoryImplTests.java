package com.github.youssefwadie.todo.repositories.todo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import com.github.youssefwadie.todo.repositories.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.youssefwadie.todo.repositories.BaseDaoTests;
import com.github.youssefwadie.todo.model.Todo;

public class TodoRepositoryImplTests extends BaseDaoTests {
    @Autowired
    TodoRepository todoRepository;

    @Test
    void testFindAllByUserId() {
        Long userId = 8L;
        List<Todo> userTodos = (List<Todo>) todoRepository.findAllByUserId(userId);
        assertThat(userTodos.size()).isEqualTo(0);
        userTodos.forEach(System.out::println);
    }

    @Test
    void testCountByUserId() {
        Long userId = 5L;
        long count = todoRepository.countByUserId(userId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testDeleteAllIdsByUserId() {
        Long userId = 7L;
        long todosCountBefore = todoRepository.count();
        todoRepository.deleteAllByUserId(userId);
        long todosCountAfter = todoRepository.count();
        System.out.println("old count = " + todosCountBefore);
        System.out.println("new count = " + todosCountAfter);
        assertThat(todosCountBefore).isEqualTo(todosCountAfter);
    }

    @Test
    public void testBelongsToUser() {
        Long userId = 1L;
        Long id = 7L;
        boolean belongs = todoRepository.belongsToUser(id, userId);
        assertThat(belongs).isFalse();
    }

    @Test
    public void testDeleteAllByUserId() {
        Long userId = 2L;
        long countBefore = todoRepository.count();
        todoRepository.deleteAllByUserId(userId);
        long countAfter = todoRepository.count();
        System.out.println("Before deletion: " + countBefore);
        System.out.println("After deletion: " + countAfter);
        assertThat(countBefore - countAfter).isEqualTo(0);
    }
}

