package com.github.youssefwadie.todo.config;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.github.youssefwadie.todo.repositories")
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

}
