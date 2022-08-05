import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/User';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  user: User;

  constructor() {}

  ngOnInit(): void {
    this.user = new User();
  }

  onLogin(): void {
    console.log(`email = ${this.user.email}, password = ${this.user.password}`);
  }
}
