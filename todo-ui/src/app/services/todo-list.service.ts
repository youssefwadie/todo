import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app-constants";
import {BehaviorSubject, map, Observable, of} from "rxjs";
import {TodoItem} from "../model/TodoItem";

@Injectable({
    providedIn: 'root'
})
export class TodoListService {
    private todoList$ = new BehaviorSubject<TodoItem[]>(new Array<TodoItem>());

    constructor(private http: HttpClient) {
    }

    public init(): void {
        this.http.get<TodoItem[]>(environment.rootURL + AppConstants.API_TODO_LIST_PATH)
            .subscribe((todoList) => {
                this.todoList$.next(todoList);
            });
    }

    public getTodoList(): Observable<TodoItem[]> {
        return this.todoList$;
    }

    findTodoById(id: number): Observable<TodoItem> {
        let item: Observable<TodoItem> | undefined = undefined;
        this.todoList$
            .pipe(map((todoList) => todoList.find((todoItem) => todoItem.id === id)))
            .subscribe(todoItem => {
                if (todoItem) {
                    item = of(todoItem);
                }
            });

        if (item) {
            return item;
        }
        return this.http.get<TodoItem>(environment.rootURL + AppConstants.API_TODO_LIST_FIND_BY_ID + id);
    }

    updateTodo(todoItem: TodoItem): Observable<TodoItem> {
        return this.http.put<TodoItem>(environment.rootURL + AppConstants.API_UPDATE_TODO, todoItem);
    }

    updateTodoItemStatus(todoItem: TodoItem): Observable<any> {
        return this.http.put<TodoItem>(environment.rootURL + AppConstants.API_UPDATE_TODO_STATUS_PATH(todoItem.id, todoItem.done), {});
    }
}
