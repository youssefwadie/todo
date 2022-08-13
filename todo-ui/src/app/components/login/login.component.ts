import {Component, OnInit} from '@angular/core';
import {User} from 'src/app/model/User';
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {DataService} from "../../services/data.service";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
    user: User;
    validLogin = true;


    constructor(private authService: AuthService, private router: Router, private dataService: DataService) {
    }

    ngOnInit(): void {
        this.user = new User();
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['list']);
        }
    }

    onLogin(): void {
        this.authService.login(this.user).subscribe(
            {
                next: (value) => {

                    this.router.navigate(['list']);
                }
            }
        );

        // this.validLogin = this.authService.onLogin(this.user);
        // if (this.validLogin) {
        //   this.dataService.getUserByEmail(this.user.email).subscribe({
        //     next: (user) => {
        //       this.user = user;
        //       const json = JSON.stringify(this.user);
        //       sessionStorage.setItem('loggedUser', json);
        //       this.router.navigate(['list']);
        //     },
        //     error: (err) => {
        //       console.log(err)
        //       this.router.navigate(['login']);
        //     }
        //   });
        // }
    }

}
