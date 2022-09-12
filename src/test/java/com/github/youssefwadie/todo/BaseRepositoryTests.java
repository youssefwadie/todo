package com.github.youssefwadie.todo;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;

@ComponentScan(basePackages = {"com.github.youssefwadie.todo.config",
        "com.github.youssefwadie.todo.todoitem.dao",
        "com.github.youssefwadie.todo.user.dao",
        "com.github.youssefwadie.todo.user.role",
        "com.github.youssefwadie.todo.user.confirmationtoken.dao"})
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
public abstract class BaseRepositoryTests {
}
