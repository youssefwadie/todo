import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {TodoItem} from '../../model/TodoItem';
import {faPlusSquare} from "@fortawesome/free-regular-svg-icons";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TodoEditComponent} from './todo-edit/todo-edit.component';
import {TodoListService} from '../../services/todo-list.service';

@Component({
  selector: 'app-todo-list-list',
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.css'],
})
export class TodoListComponent implements OnInit, OnDestroy {
  selectedTodoItem: TodoItem;
  action: string;
  public todoList: Array<TodoItem>;
  private todoListSubscription: Subscription;

  faPlus = faPlusSquare;

  constructor(
    private router: Router,
    private todoListService: TodoListService,
    private route: ActivatedRoute,
    private modalService: NgbModal
  ) {
  }

  ngOnInit(): void {
    this.todoListSubscription = this.todoListService.getTodoList().subscribe(
      {
        next: next => {
          this.todoList = next;
        }
      }
    );

    this.route.queryParams.subscribe((params) => {
      const id = params['id'];
      this.action = params['action'];
      if (id) {
        this.todoListService.findTodoById(+id).subscribe((fetchedTodo) => {
          this.selectedTodoItem = fetchedTodo;
          this.open();
        });
      } else {
        if (this.action === 'add') {
          this.selectedTodoItem = new TodoItem();
          this.selectedTodoItem.deadTime = new Date();
          this.open();
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.todoListSubscription.unsubscribe();
  }

  public addTodo(): void {
    this.router.navigate([''], {
      queryParams: {action: 'add'},
    });
  }

  open() {
    const modalRef = this.modalService.open(TodoEditComponent);
    modalRef.componentInstance.todoItem = this.selectedTodoItem;
    modalRef.result.then(
      () => {
      },
      (reason) => {
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {},
        });
      }
    );
    // modalRef.dismiss((reason: any) => {
    //     console.log('dismssed');
    //     this.router.navigate(['list']);
    // });
  }
}
