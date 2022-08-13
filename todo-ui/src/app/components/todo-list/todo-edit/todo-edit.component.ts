import {Component, Input, OnInit} from '@angular/core';
import {TodoItem} from "../../../model/TodoItem";
import {NgbActiveModal, NgbDateStruct, NgbTimeStruct} from "@ng-bootstrap/ng-bootstrap";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";

@Component({
    selector: 'app-todo-edit',
    templateUrl: './todo-edit.component.html',
    styleUrls: ['./todo-edit.component.css']
})
export class TodoEditComponent implements OnInit {

    @Input() todo: TodoItem;
    time: NgbTimeStruct;
    date: NgbDateStruct;
    faCalendarDays = faCalendarDays;


    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        let deadTime = this.todo.deadTime;
        if (deadTime) {
            deadTime = new Date(deadTime);
        } else {
            deadTime = new Date();
        }

        this.time = {hour: deadTime.getHours(), minute: deadTime.getMinutes(), second: deadTime.getSeconds()};
        this.date = {year: deadTime.getFullYear(), month: deadTime.getMonth(), day: deadTime.getDay()}
    }

    onSubmit(): void {
        console.log(`Submitting... ${this.todo}`)
        console.log(`Date: ${this.date.day}-${this.date.month}`);
        console.log(`Time: ${this.time.hour}:${this.time.minute}:${this.time.second}`);

    }
}
