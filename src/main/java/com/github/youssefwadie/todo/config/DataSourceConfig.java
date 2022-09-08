package com.github.youssefwadie.todo.config;


import com.github.youssefwadie.todo.dao.todo.TodoItemRowMapper;
import com.github.youssefwadie.todo.dao.user.UserRowMapper;
import com.github.youssefwadie.todo.model.TodoItem;
import com.github.youssefwadie.todo.model.User;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.config.DefaultQueryMappingConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableJdbcRepositories(basePackages = "com.github.youssefwadie.todo.dao")
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
