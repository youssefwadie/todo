import {Component, Input, OnInit} from '@angular/core';
import {Todo} from "../../model/Todo";
import {AppConstants} from "../../constants/app-constants";
import {faCalendarDays, faCheckCircle} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-todo',
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css']
})
export class TodoComponent implements OnInit {

  @Input()
  todo: Todo;
  shortDescription: string;
  shortTitle: string;
  isCollapsed = true;
  isCollapsable = false;
  deadTime: string;
  faCalendarDays = faCalendarDays;
  faCheckCircle = faCheckCircle;

  constructor() {
  }

  ngOnInit(): void {
    if (this.todo) {
      if (this.todo.description.length > 70) {
        this.shortDescription = this.todo.description.slice(0, 70) + "...";
        this.isCollapsable = true;
      } else {
        this.shortDescription = this.todo.description;
        this.isCollapsable = false;
      }

      if (this.todo.title.length > 35) {
        this.shortTitle = this.todo.title.slice(0, 35) + '...';
      } else {
        this.shortTitle = this.todo.title;
      }

      this.deadTime = new Date(this.todo.deadTime).toLocaleDateString(AppConstants.localArguments, AppConstants.dateTimeFormatOptions);
    }
  }

  toggleTodo(): void {
    this.todo.done = !this.todo.done;
  }
}
