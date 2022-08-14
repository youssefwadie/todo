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


    constructor(public activeModal: NgbActiveModal, private todoListService: TodoListService) {
    }

    ngOnInit(): void {
        this.formTodoItem = Object.assign({}, this.todoItem);

        let deadTime = this.formTodoItem.deadTime;
        if (deadTime) {
            deadTime = new Date(deadTime);
        } else {
            deadTime = new Date();
        }
        this.time = {hour: deadTime.getHours(), minute: deadTime.getMinutes(), second: deadTime.getSeconds()};
        this.date = {year: deadTime.getFullYear(), month: deadTime.getMonth(), day: deadTime.getDay()}


        this.checkIfTitleIsValid();
    }

    onSubmit(): void {
        this.formTodoItem.title = this.formTodoItem.title.trim();
        this.formTodoItem.description = this.formTodoItem.description.trim();

        this.todoListService.updateTodoItemStatus(this.formTodoItem).subscribe({
                next: value => {
                    this.todoItem = value
                },
                error: err => console.log(err)
            }
        );
    }

    checkIfTitleIsValid(): void {
        this.validTitle = this.formTodoItem.title.trim().length >= 10;
    }
}
