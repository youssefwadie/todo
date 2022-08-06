import {Todo} from "./Todo";

export class User {
    email: string;
    password: string;
    todoList = new Array<Todo>();
}
