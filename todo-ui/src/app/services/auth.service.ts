import {Injectable} from '@angular/core';
import {User} from "../model/User";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {EMPTY, Observable, tap} from "rxjs";
import {AppConstants} from "../constants/app-constants";
import * as moment from "moment/moment";
import {Moment} from "moment/moment";
import {Router} from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    public currentUser: User;

    constructor(private http: HttpClient, private router: Router) {
    }

    login(user: User): Observable<any> {
        return this.http
            .post<User>(environment.rootURL + AppConstants.API_ACCOUNT_LOGIN_PATH, user, {
                observe: 'response',
                withCredentials: true,
                headers: {
                    'Authorization': 'Basic ' + btoa(user.email + ':' + user.password)
                }
            })
            .pipe(tap(res => this.setSession(res)));
    }

    public isLoggedIn() {
        const userLoggedIn = moment().isBefore(this.getExpiration());
        if (userLoggedIn) {
            if (this.currentUser != null) {
                return true;
            }

            const accessToken = localStorage.getItem('access_token');
            if (accessToken) {
                const parsedUser = this.parseUser(accessToken);
                if (parsedUser) {
                    this.currentUser = parsedUser;
                }
                return true;
            }
        }
        return false;
    }

    public logout(): void {
        localStorage.removeItem('access_token');
    }

    private setSession(authResult: HttpResponse<any>) {
        const jwt = this.parseJwt(authResult.body.access_token);
        if (jwt) {
            const {exp} = jwt;
            localStorage.setItem('access_token', authResult.body.access_token);
            localStorage.setItem('expires_at', JSON.stringify(exp * 1000));
            const parsedUser = this.parseUser(jwt);
            if (parsedUser) {
                this.currentUser = parsedUser;
            }
        }
    }

    private parseUser(token: string): User | null {
        const jwt = this.parseJwt(token);
        if (jwt) {
            const user = new User();
            const {sub} = jwt;
            user.email = sub;
            return user;
        }
        return null;
    }

    private parseJwt(token: string): any | null {
        try {
            return JSON.parse(atob(token.split('.')[1]));
        } catch (e) {
            return null;
        }
    }


    private getExpiration(): Moment {
        const expiration = localStorage.getItem("expires_at");
        if (expiration) {
            const expiresAt = JSON.parse(expiration);
            if (expiresAt) {
                return moment(expiresAt);
            }
        }

        return moment().subtract(1, 'days');
    }


}
