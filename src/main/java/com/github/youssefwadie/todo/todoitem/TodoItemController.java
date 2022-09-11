package com.github.youssefwadie.todo.todoitem;

import com.github.youssefwadie.todo.model.TodoItem;
import com.github.youssefwadie.todo.model.User;
import com.github.youssefwadie.todo.security.TodoUserDetails;
import com.github.youssefwadie.todo.security.exceptions.ConstraintsViolationException;
import com.github.youssefwadie.todo.util.SimpleResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/todo", produces = "application/json")
public class TodoItemController {

    private final TodoItemService todoItemService;


    @GetMapping(value = "", produces = "application/json")
    public ResponseEntity<List<TodoItem>> list() {
        User loggedUser = getLoggedUser();
        return ResponseEntity.ok(todoItemService.findAllByUserId(loggedUser.getId()));
    }


    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createTodo(@RequestBody TodoItem todo) throws URISyntaxException {
        try {
            if (todo.getId() != null) return ResponseEntity.badRequest().build();

            User loggedUser = getLoggedUser();
            todo.setUserId(loggedUser.getId());
            TodoItem savedTodo = todoItemService.save(todo);
            URI saveTodoURI = new URI("/api/v1/todo/" + savedTodo.getId());
            return ResponseEntity.created(saveTodoURI).body(savedTodo);
        } catch (ConstraintsViolationException ex) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ex.getErrors());
        }
    }

    @GetMapping(path = "/{id:\\d+}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") Long id) {
        User loggedUser = getLoggedUser();
        if (todoItemService.notOwnedByUser(id, loggedUser.getId())) {
            return notFoundTodoItem(id);
        }

        Optional<TodoItem> optionalTodoItem = todoItemService.findById(id);

        // if it got here, the item will eventually be there
        return ResponseEntity.ok(optionalTodoItem.get());
    }

    @PutMapping("")
    public ResponseEntity<?> updateTodoItem(@RequestBody TodoItem todoItem) throws ConstraintsViolationException {
        User loggedUser = getLoggedUser();
        if (todoItem.getId() == null) return ResponseEntity.badRequest().build();
        if (todoItemService.notOwnedByUser(todoItem.getId(), loggedUser.getId())) return notFoundTodoItem(todoItem.getId());

        todoItem.setUserId(loggedUser.getId());
        return ResponseEntity.ok(todoItemService.save(todoItem));
    }

    @PutMapping(path = "/{id:\\d+}/{done:true|false}")
    public ResponseEntity<?> updateTodoItemStatus(@PathVariable("id") Long id, @PathVariable("done") boolean done) {
        User loggedUser = getLoggedUser();

        if (todoItemService.notOwnedByUser(id, loggedUser.getId())) {
            return notFoundTodoItem(id);
        }
        todoItemService.setDone(id, done);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private ResponseEntity<?> notFoundTodoItem(Long id) {
        SimpleResponseBody responseBody = new SimpleResponseBody
                .Builder(HttpStatus.NO_CONTENT.value(), HttpStatus.NOT_FOUND.getReasonPhrase())
                .setMessage("No TodoItem with id: %d was found".formatted(id))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    private User getLoggedUser() {
        TodoUserDetails loggedInPrincipal = (TodoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loggedInPrincipal.getUser();
    }

}
