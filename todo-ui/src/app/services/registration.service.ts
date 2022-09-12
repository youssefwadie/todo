import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserRegistration} from "../model/UserRegistration";
import {User} from "../model/User";
import {environment} from "../../environments/environment";
import {AppConstants} from "../constants/app-constants";
import {Observable} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private http: HttpClient) {
  }

  register(user: UserRegistration): Observable<User> {
    return this.http.post<User>(environment.rootURL + AppConstants.API_REGISTRATION, user);
  }
}
