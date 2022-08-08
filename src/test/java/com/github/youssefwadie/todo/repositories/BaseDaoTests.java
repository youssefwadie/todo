package com.github.youssefwadie.todo.repositories;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;

@ComponentScan(basePackages = {"com.github.youssefwadie.todo.config", "com.github.youssefwadie.todo.repositories"})
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback
public abstract class BaseDaoTests {
}
