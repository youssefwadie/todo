package com.github.youssefwadie.todo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
public class Todo {
    @Id
    private Long id;

    @Basic
    private String title;

    @Basic
    private String description;

    @Basic
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd:HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deadTime;

    @Basic
    private Boolean done;

    public Todo() {
    }

    public Todo(Long id, String title, String description, LocalDateTime deadTime, boolean done, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadTime = deadTime;
        this.done = done;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(LocalDateTime deadTime) {
        this.deadTime = deadTime;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "Todo{" + "id=" + id + ", title='" + title + '\'' + ", description='" + description + '\'' + ", userId="
                + userId + ", deadTime=" + deadTime + '}';
    }
}
