import {Component, Input, OnInit} from '@angular/core';
import {TodoItem} from "../../../model/TodoItem";
import {AppConstants} from "../../../constants/app-constants";
import {faBook, faCheckCircle, faPenSquare} from "@fortawesome/free-solid-svg-icons";
import {Router} from "@angular/router";
import {TodoListService} from "../../../services/todo-list.service";

@Component({
  selector: 'app-todo-detail',
  templateUrl: './todo-detail.component.html',
  styleUrls: ['./todo-detail.component.css']
})
export class TodoDetailComponent implements OnInit {

  @Input()
  todoItem: TodoItem;

  shortTitle: string;
  isCollapsed = true;
  deadTime: string;

  faCheckCircle = faCheckCircle;
  faBook = faBook;
  faPenSquare = faPenSquare;


  constructor(private todoListService: TodoListService, private router: Router) {
  }

  ngOnInit(): void {
    if (this.todoItem) {
      this.deadTime = new Date(this.todoItem.deadTime).toLocaleDateString(AppConstants.LOCALES_ARGUMENT, AppConstants.DATE_TIME_FORMAT_OPTIONS);
      if (this.todoItem.title.length > 70) {
        this.shortTitle = this.todoItem.title.slice(0, 70) + '...';
      } else {
        this.shortTitle = this.todoItem.title;
      }
    }
  }

  toggleTodo(): void {
    this.todoListService.updateTodoItemStatus(this.todoItem)
      .subscribe({
        next: (isUpdated) => {
          if (isUpdated) {
            this.todoItem.done = !this.todoItem.done;
          }
          this.router.navigate(['']);
        }, error: err => {
          console.log(err);
        }
      });
  }

  editTodo(): void {
    this.router.navigate([''], {queryParams: {'id': this.todoItem.id, 'action': 'edit'}});
  }
}
