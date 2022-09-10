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

    let deadTime = this.formTodoItem.deadTime;
    if (deadTime) {
      deadTime = new Date(deadTime);
    } else {
      deadTime = new Date();
    }
    this.time = {hour: deadTime.getUTCHours(), minute: deadTime.getUTCMinutes(), second: deadTime.getUTCSeconds()};
    this.date = {year: deadTime.getFullYear(), month: deadTime.getMonth() + 1, day: deadTime.getUTCDate()}

    this.checkIfTitleIsValid();
  }

  onSubmit(): void {
    this.formTodoItem.title = this.formTodoItem.title.trim();
    this.formTodoItem.description = this.formTodoItem.description.trim();

    this.formTodoItem.deadTime = new Date(this.date.year, this.date.month, this.date.day, this.time.hour, this.time.minute, this.time.second);
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
