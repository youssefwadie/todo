import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app-constants";
import {Observable} from "rxjs";
import {TodoItem} from "../model/TodoItem";

@Injectable({
    providedIn: 'root'
})
export class TodoListService {


    constructor(private http: HttpClient) {

    }

    public getTodoList(): Observable<any> {
        return this.http.get(environment.rootURL + AppConstants.API_TODO_LIST_PATH);
    }

    findTodoById(id: number): Observable<TodoItem> {
        return this.http.get<TodoItem>(environment.rootURL + AppConstants.API_TODO_LIST_FIND_BY_ID + id);
    }

    updateTodo(todoItem: TodoItem): Observable<TodoItem> {
        return this.http.put<TodoItem>(environment.rootURL + AppConstants.API_UPDATE_TODO, todoItem);
    }

}
