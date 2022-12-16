import {Component, Input, OnInit} from '@angular/core';
import {TodoItem} from "../../../model/TodoItem";
import {NgbActiveModal, NgbDateStruct, NgbTimeStruct} from "@ng-bootstrap/ng-bootstrap";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";
import {TodoListService} from "../../../services/todo-list.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-todo-edit',
  templateUrl: './todo-edit.component.html',
  styleUrls: ['./todo-edit.component.css']
})
export class TodoEditComponent implements OnInit {

  @Input() todoItem: TodoItem;
  formTodoItem: TodoItem;
  validTitle = true;
  time: NgbTimeStruct;
  date: NgbDateStruct;
  faCalendarDays = faCalendarDays;


  constructor(private router: Router, public activeModal: NgbActiveModal, private todoListService: TodoListService) {
  }

  ngOnInit(): void {
    this.formTodoItem = Object.assign({}, this.todoItem);
    let deadline = this.formTodoItem.deadline;
    if (deadline) {
      deadline = new Date(deadline);
    } else {
      deadline = new Date();
    }

    this.time = {hour: deadline.getHours(), minute: deadline.getMinutes(), second: deadline.getHours()};
    this.date = {year: deadline.getFullYear(), month: deadline.getMonth(), day: deadline.getUTCDate()};

    this.checkIfTitleIsValid();
  }

  onSubmit(): void {
    this.formTodoItem.title = this.formTodoItem.title.trim();
    this.formTodoItem.description = this.formTodoItem.description.trim();

    this.formTodoItem.deadline = new Date(this.date.year, this.date.month, this.date.day, this.time.hour, this.time.minute, this.time.second);
    console.log(this.formTodoItem.deadline);
    if (this.formTodoItem.id != null) {
      this.todoListService.updateTodo(this.formTodoItem)
        .subscribe({
          next: value => {
            this.todoItem = value;
            this.router.navigate(['']);
          },
          error: err => console.log(err)
        });
    } else {
      this.todoListService.addTodo(this.formTodoItem)
        .subscribe({
          next: value => {
            this.todoItem = value;
            this.router.navigate(['']);
          },
          error: err => console.log(err)
        });
    }
  }

  checkIfTitleIsValid(): void {
    if (this.formTodoItem.title) {
      this.validTitle = this.formTodoItem.title.trim().length >= 10;
    }
  }
}
