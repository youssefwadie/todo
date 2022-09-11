package com.github.youssefwadie.todo.todoitem;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import com.github.youssefwadie.todo.todoitem.dao.TodoItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.youssefwadie.todo.BaseRepositoryTests;
import com.github.youssefwadie.todo.model.TodoItem;

public class TodoItemRepositoryImplTests extends BaseRepositoryTests {
    @Autowired
    TodoItemRepository todoDao;

    @Test
    void testFindAllByUserId() {
        Long userId = 8L;
        List<TodoItem> userTodos = (List<TodoItem>) todoDao.findAllByUserId(userId);
        assertThat(userTodos.size()).isEqualTo(0);
        userTodos.forEach(System.out::println);
    }

    @Test
    void testCountByUserId() {
        Long userId = 5L;
        long count = todoDao.countByUserId(userId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testDeleteAllIdsByUserId() {
        Long userId = 7L;
        long itemsCount = todoDao.count();
        todoDao.deleteAllByUserId(userId);
        long itemsCountAfter = todoDao.count();
        System.out.println("old count = " + itemsCount);
        System.out.println("new count = " + itemsCountAfter);
        assertThat(itemsCount).isEqualTo(itemsCountAfter);
    }

    @Test
    public void testBelongsToUser() {
        Long userId = 1L;
        Long id = 7L;
        boolean belongs = todoDao.ownedByUser(id, userId);
        assertThat(belongs).isFalse();
    }

    @Test
    public void testDeleteAllByUserId() {
        Long userId = 2L;
        long countBefore = todoDao.count();
        todoDao.deleteAllByUserId(userId);
        long countAfter = todoDao.count();
        System.out.println("Before deletion: " + countBefore);
        System.out.println("After deletion: " + countAfter);
        assertThat(countBefore - countAfter).isEqualTo(0);
    }
}

