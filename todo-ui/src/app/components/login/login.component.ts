import {Component, OnInit} from '@angular/core';
import {User} from 'src/app/model/User';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  user: User;
  validLogin = true;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.user = new User();
  }

  onLogin(): void {
    this.authService.login(this.user).subscribe({
      next: (user) => {
        // this.user = user;
        this.router.navigate(['']);
      },
      error: (err: HttpErrorResponse) => {
        this.validLogin = false;
        if (err.status === 401) {
          this.errorMessage = 'Invalid Email or Password';
        } else {
          this.errorMessage = 'Unknown error';
        }
        console.log(err);
      },
    });
  }
}
