package com.github.youssefwadie.todo.todoitem;

import com.github.youssefwadie.todo.model.TodoItem;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class TodoItemValidatorService {
    private static final String INVALID_STARTING_CHARS = "\\s@#\\$%\\^&-+=\\(\\)";
    public static final String TODO_TITLE_VALIDATION_MESSAGE = String.format("A todo title cannot be blank or starts with %s", INVALID_STARTING_CHARS);
    private static final String TODO_TITLE_REGEX = "^(?![" + INVALID_STARTING_CHARS + "]).*$";
    private static final Pattern TODO_TITLE_PATTERN = Pattern.compile(TODO_TITLE_REGEX);


    public static final String TODO_DESCRIPTION_VALIDATION_MESSAGE = "A todo description cannot be blank";
    private static final String TODO_DESCRIPTION_REGX = "^(?!\\s*$).+";
    private static final Pattern TODO_DESCRIPTION_PATTERN = Pattern.compile(TODO_DESCRIPTION_REGX);

    public static final String TODO_DEADLINE_VALIDATION_MESSAGE = "A todo deadline cannot be null or in the past";

    public boolean isValidTitle(String title) {
        return title != null && TODO_TITLE_PATTERN.matcher(title).matches();
    }

    private final Clock clock;

    public boolean isValidDescription(String description) {
        return description != null && TODO_DESCRIPTION_PATTERN.matcher(description).matches();
    }

    public boolean isValidDeadline(LocalDateTime deadline) {
        return deadline != null && deadline.isAfter(LocalDateTime.now(clock));
    }

    public void validateTodo(TodoItem todo, boolean checkDate) throws ConstraintsViolationException {
        Map<String, String> errors = new HashMap<>();

        if (!isValidTitle(todo.getTitle())) {
            errors.put("title", TodoItemValidatorService.TODO_TITLE_VALIDATION_MESSAGE);
        }

        if (!isValidDescription(todo.getDescription())) {
            errors.put("description", TodoItemValidatorService.TODO_DESCRIPTION_VALIDATION_MESSAGE);
        }

        if (checkDate && !isValidDeadline(todo.getDeadline())) {
            errors.put("deadline", TodoItemValidatorService.TODO_DEADLINE_VALIDATION_MESSAGE);
        }

        if (!errors.isEmpty()) {
            throw new ConstraintsViolationException(errors);
        }
    }

}
