import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app-constants";
import {BehaviorSubject, catchError, map, Observable, of, tap} from "rxjs";
import {TodoItem} from "../model/TodoItem";

@Injectable({
  providedIn: 'root'
})
export class TodoListService {
  private backingTodoList = new Array<TodoItem>();
  private readonly todoList$: BehaviorSubject<TodoItem[]>;

  constructor(private http: HttpClient) {
    this.todoList$ = new BehaviorSubject<TodoItem[]>(new Array<TodoItem>());
    this.init();
  }

  public init(): void {
    this.http.get<TodoItem[]>(environment.rootURL + AppConstants.API_TODO_LIST_PATH)
      .pipe(map(this.arrayFromHttp.bind(this))).subscribe(todoList => {
      this.backingTodoList = todoList;
      this.todoList$.next(this.backingTodoList);
    });
  }

  private arrayFromHttp(items: Array<TodoItem>): Array<TodoItem> {
    return items.map(this.fromHttp);
  }

  private fromHttp(item: TodoItem): TodoItem {
    return new TodoItem(item.id, item.title, item.description, new Date(item.deadline), item.done);
  }

  public getTodoList(): Observable<TodoItem[]> {
    return this.todoList$.asObservable();
  }

  findTodoById(id: number): Observable<TodoItem> {
    const index = this.backingTodoList.findIndex(item => item.id === id);
    if (index === -1) {
      return this.http.get<TodoItem>(environment.rootURL + AppConstants.API_TODO_LIST_FIND_BY_ID + id)
        .pipe(map(this.fromHttp.bind(this)));
    } else {
      return of(this.backingTodoList[index]);
    }
  }

  updateTodo(todoItem: TodoItem): Observable<TodoItem> {
    return this.http.put<TodoItem>(environment.rootURL + AppConstants.API_UPDATE_TODO, todoItem)
      .pipe(map(this.fromHttp)).pipe(tap(this.replaceTodoItem.bind(this)));
  }

  private replaceTodoItem(todoItem: TodoItem): void {
    if (!todoItem) return;
    const index = this.backingTodoList.findIndex(item => item.id === todoItem.id);
    if (index === -1) {
      this.backingTodoList.push(todoItem);
    } else {
      this.backingTodoList[index] = todoItem;
    }

    this.todoList$.next(this.backingTodoList);
  }

  updateTodoItemStatus(todoItem: TodoItem): Observable<boolean> {
    return this.http
      .put<TodoItem>(environment.rootURL + AppConstants.API_UPDATE_TODO_STATUS_PATH(todoItem.id, !todoItem.done), {})
      .pipe(map(res => true))
      .pipe(catchError(err => of(false)));

  }

  addTodo(newItem: TodoItem) {
    return this.http.post<TodoItem>(environment.rootURL + AppConstants.API_ADD_TODO, newItem)
      .pipe(map(this.fromHttp)).pipe(tap(this.replaceTodoItem.bind(this)));

  }
}
