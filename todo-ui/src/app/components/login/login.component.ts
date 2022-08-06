import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {User} from 'src/app/model/User';
import {Router} from "@angular/router";
import {LoginService} from "../../services/login.service";
import {DataService} from "../../services/data.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  user: User;
  validLogin = true;


  constructor(private loginService: LoginService, private dataService: DataService, private router: Router) {
  }

  ngOnInit(): void {
    this.user = new User();
  }

  onLogin(): void {
    this.validLogin = this.loginService.onLogin(this.user);
    if (this.validLogin) {
      this.user = this.dataService.getUserByEmail(this.user.email);
      const json = JSON.stringify(this.user);
      sessionStorage.setItem('loggedUser', json);
      this.router.navigate(['dashboard']);
    }
  }

}
