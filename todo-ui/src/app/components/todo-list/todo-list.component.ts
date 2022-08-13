import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../model/User";
import {Subscription} from "rxjs";
import {DataService} from "../../services/data.service";
import {TodoItem} from "../../model/TodoItem";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {TodoEditComponent} from "./todo-edit/todo-edit.component";
import {TodoListService} from "../../services/todo-list.service";
import {AuthService} from "../../services/auth.service";

@Component({
    selector: 'app-todo-list-list',
    templateUrl: './todo-list.component.html',
    styleUrls: ['./todo-list.component.css']
})
export class TodoListComponent implements OnInit, OnDestroy {
    user: User;
    todoEditSubscription: Subscription;
    selectedTodo: TodoItem;
    action: string;
    todoList = new Array<TodoItem>();

    constructor(private router: Router,
                private dataService: DataService,
                private todoListService: TodoListService,
                private route: ActivatedRoute,
                private modalService: NgbModal,
                private authService: AuthService) {
    }

    ngOnInit(): void {
        this.todoListService.getTodoList().subscribe(
            res => {
                if (res) {
                    res.forEach((todoItem: TodoItem) => this.todoList.push(todoItem));
                }
            }
        );
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['login']);
            return;
        }

        this.user = this.authService.currentUser;

        this.todoEditSubscription = this.dataService.todoEditEvent.subscribe((todo) => {
            this.selectedTodo = todo;
        });
        this.route.queryParams.subscribe((params) => {
            const id = params['id'];
            this.action = params['action'];
            if (id) {
                this.todoListService.findTodoById(+id).subscribe((fetchedTodo) => {
                        this.selectedTodo = fetchedTodo;
                        this.open();
                    }
                );
            // } else {
                // this.addTodo();
            }
        });

    }

    ngOnDestroy(): void {
        // this.todoEditSubscription.unsubscribe();
    }

    private addTodo(): void {
        this.selectedTodo = new TodoItem(undefined, undefined, undefined, undefined);
        this.router.navigate(['list'], {
            queryParams: {action: 'add'},
        });

    }

    open() {
        const modalRef = this.modalService.open(TodoEditComponent);
        modalRef.componentInstance.todo = this.selectedTodo;
    }


}
