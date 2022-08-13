import {EventEmitter, Injectable} from '@angular/core';
import {User} from "../model/User";
import {TodoItem} from "../model/TodoItem";
import {Observable, of, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private _users = new Array<User>();
  todoEditEvent = new EventEmitter<TodoItem>();


  constructor() {
  }

  validateUserCredentials(user: User): boolean {

    for (const u of this._users) {
      if (u.email === user.email) {
        return u.password === user.password;
      }
    }
    return false;
  }

  getUserByEmail(email: string): Observable<User> {
    for (const user of this._users) {
      if (user.email === email) {
        return of(user);
      }
    }

    return throwError(() => `no todo with email: ${email} was found`);
  }

  toggleTodo(todo: TodoItem): Observable<TodoItem> {
    this.findTodoById(todo.id).subscribe(
      {
        next: originalTodo => {
          originalTodo.done = !todo.done;
          return of(originalTodo);
        }, error: (err) => {
          console.log(err);
          return throwError(() => err);
        }
      }
    );

    return throwError(() => `no todo with id: ${todo.id} was found`);
  }

  updateTodo(todo: TodoItem): Observable<TodoItem> {
    this.findTodoById(todo.id).subscribe(
      {
        next: (originalTodo) => {
          originalTodo.title = todo.title;
          originalTodo.done = todo.done;
          originalTodo.deadTime = todo.deadTime;
          originalTodo.description = todo.description;
          return of(originalTodo);
        },
        error: (err) => {
          return throwError(() => err);
        }
      },
    );

    return throwError(() => `no todo with id: ${todo.id} was found`);
  }

  findTodoById(id: number): Observable<TodoItem> {
    // for (const user of this._users) {
    //   for (const todo of user.todoList) {
    //     if (todo.id === id) {
    //       return of(todo);
    //     }
    //   }
    // }
    return throwError(() => `no todo with id: ${id} was found`);
  }
}
