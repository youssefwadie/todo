import {Component, EventEmitter, Input, OnDestroy, OnInit} from '@angular/core';
import {TodoItem} from "../../../model/TodoItem";
import {AppConstants} from "../../../constants/app-constants";
import {faBook, faCheckCircle, faPenSquare} from "@fortawesome/free-solid-svg-icons";
import {DataService} from "../../../services/data.service";
import {Router} from "@angular/router";
import {TodoListService} from "../../../services/todo-list.service";

@Component({
    selector: 'app-todo-detail',
    templateUrl: './todo-detail.component.html',
    styleUrls: ['./todo-detail.component.css']
})
export class TodoDetailComponent implements OnInit {

    @Input()
    todo: TodoItem;

    shortTitle: string;
    isCollapsed = true;
    deadTime: string;

    faCheckCircle = faCheckCircle;
    faBook = faBook;
    faPenSquare = faPenSquare;


    constructor(private dataService: DataService, private todoListService: TodoListService, private router: Router) {
    }

    ngOnInit(): void {
        if (this.todo) {
            this.deadTime = new Date(this.todo.deadTime).toLocaleDateString(AppConstants.LOCALES_ARGUMENT, AppConstants.DATE_TIME_FORMAT_OPTIONS);
            if (this.todo.title.length > 70) {
                this.shortTitle = this.todo.title.slice(0, 70) + '...';
            } else {
                this.shortTitle = this.todo.title;
            }
        }
    }

    toggleTodo(): void {
        this.todo.done = !this.todo.done;
        this.todoListService.updateTodo(this.todo)
            .subscribe((updatedTodo) => {
                this.todo = updatedTodo;
                this.router.navigate(['list']);
            });
    }

    editTodo(): void {
        this.router.navigate(['list'], {queryParams: {'id': this.todo.id, 'action': 'edit'}})
    }

}
