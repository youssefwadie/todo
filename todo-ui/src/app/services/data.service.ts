import {Injectable} from '@angular/core';
import {User} from "../model/User";
import {LoginService} from "./login.service";
import {Todo} from "../model/Todo";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private _users = new Array<User>();

  constructor() {
    const user1 = new User();
    user1.email = "user1@mail.com";
    user1.password = "12345";

    for (let i = 1; i <= 20; i++) {
      let description: string;
      if (i % 2 === 0) {
        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
      } else {
        description = `User 1 todo ${i} - description`;
      }

      let title: string;
      title = `User 1 - todo ${i}`;
      if (i === 5) {
        title = title.repeat(5);
      }

      const todo = new Todo(title,
        description,
        new Date(2022, 8, 13 + i, 5, 0, 0, 0)
      );

      user1.todoList.push(todo);
    }

    const user2 = new User();
    user2.email = "user2@mail.com";
    user2.password = "54321";

    for (let i = 1; i <= 20; i++) {
      const todo = new Todo(`User 2 - todo ${i}`,
        `User 2 todo ${i} - description`,
        new Date(2022, 9, i, 5, 0, 0, 0)
      );

      user2.todoList.push(todo);
    }

    this._users.push(user1, user2);
  }

  validateUserCredentials(user: User): boolean {

    for (const u of this._users) {
      if (u.email === user.email) {
        return u.password === user.password;
      }
    }
    return false;
  }

  getUserByEmail(email: string): User {
    for (const user of this._users) {
      if (user.email === email) {
        return user;
      }
    }
    return new User();
  }

}
