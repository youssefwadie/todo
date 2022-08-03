package com.github.youssefwadie.todo.repositories;

import com.github.youssefwadie.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t WHERE t.userId = ?1")
    List<Todo>  findAllByUserId(Long userId);

}
