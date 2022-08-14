import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../model/User";
import {Observable} from "rxjs";
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
    selectedTodoItem: TodoItem;
    action: string;
    public todoList$: Observable<TodoItem[]>;

    constructor(private router: Router,
                private todoListService: TodoListService,
                private route: ActivatedRoute,
                private modalService: NgbModal,
                private authService: AuthService) {
    }

    ngOnInit(): void {
        if (!this.authService.isLoggedIn()) {
            this.router.navigate(['login']);
            return;
        }

        this.todoList$ = this.todoListService.getTodoList();
        this.todoListService.init();
        this.user = this.authService.currentUser;
        this.route.queryParams.subscribe((params) => {
            const id = params['id'];
            this.action = params['action'];
            if (id) {
                this.todoListService.findTodoById(+id).subscribe((fetchedTodo) => {
                        this.selectedTodoItem = fetchedTodo;
                        this.open();
                    }
                );
                // } else {
                // this.addTodo();
            }
            console.log(id, this.action)
        });

    }

    ngOnDestroy(): void {
        // this.todoEditSubscription.unsubscribe();
    }

    private addTodo(): void {
        this.selectedTodoItem = new TodoItem(undefined, undefined, undefined, undefined);
        this.router.navigate(['list'], {
            queryParams: {action: 'add'},
        });

    }

    open() {
        const modalRef = this.modalService.open(TodoEditComponent);
        modalRef.componentInstance.todoItem = this.selectedTodoItem;
        modalRef.result.then(() => {
        }, reason => {
            this.router.navigate([], {
                    relativeTo: this.route,
                    queryParams: {},
                });
        });
        // modalRef.dismiss((reason: any) => {
        //     console.log('dismssed');
        //     this.router.navigate(['list']);
        // });
    }


}
