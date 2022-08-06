import {Component, OnInit} from '@angular/core';
import {User} from "../../model/User";
import {Router} from "@angular/router";
import {Todo} from "../../model/Todo";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user = new User();
  splitTodoList = new Array<Array<Todo>>();
  private _row_weight = 3;

  constructor(private router: Router) {

  }

  ngOnInit(): void {
    const json = sessionStorage.getItem('loggedUser');
    if (json === null) {
      this.router.navigate(['login']);
    } else {
      this.user = JSON.parse(json);
      this.splitList(this.user.todoList);
    }

    console.log(`loggedUser = ${this.user}`);
  }


  private splitList(todoList: Array<Todo>): void {
    for (let i = 0; i < todoList.length; i += this._row_weight) {
      this.splitTodoList.push(todoList.slice(i, i + this._row_weight));
    }
  }

}
