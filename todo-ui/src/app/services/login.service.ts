import {Injectable} from '@angular/core';
import {User} from "../model/User";
import {DataService} from "./data.service";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private dataService: DataService) {

  }

  onLogin(user: User): boolean {
    // TODO: use the actual service
    return this.dataService.validateUserCredentials(user);
  }
}
