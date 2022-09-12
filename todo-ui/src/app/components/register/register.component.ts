import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from "../../model/User";
import {UserRegistration} from "../../model/UserRegistration";
import {RegistrationService} from "../../services/registration.service";
import {Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, OnDestroy {
  private emailPattern = /^[a-z0-9._%+-]+@[a-z0-9.-]+(\.[a-z]{2,4})+$/g;

  email = '';
  password = '';
  repeatedPassword = '';

  validRegistration = true;
  errorMessage = '';
  private registrationSubscription: Subscription;

  constructor(private registrationService: RegistrationService,
              private router: Router) {
  }


  ngOnInit(): void {
  }

  onRegister(): void {
    if (this.validFormData()) {
      // TODO: send the data to the server
      const registration: UserRegistration = {email: this.email, password: this.password};
      this.registrationSubscription = this.registrationService.register(registration).subscribe(
        {
          next: next => {
            console.log(next.email);
            this.router.navigate(['login']);
          }, error: err => {
            const errors: any = err.error;
            if (errors.hasOwnProperty('email')) {
              this.setErrorMessage(errors['email']);
            } else if (errors.hasOwnProperty('password')) {
              this.setErrorMessage(errors['password']);
            } else {
              this.setErrorMessage('An unexpected error has occurred, please try again.');
            }
          }
        }
      );
    }
  }

  public login(): void {
    this.router.navigate(['login']);
  }

  private validFormData(): boolean {
    if (!this.email.match(this.emailPattern)) {
      this.setErrorMessage("Please enter a valid email");
      return false;
    }
    if (this.password.length < 8) {
      this.setErrorMessage("Password must be at least 8 characters");
      return false;
    }
    if (this.password !== this.repeatedPassword) {
      this.setErrorMessage("The passwords don't match");
      return false;
    }
    return true;
  }


  private setErrorMessage(errorMessage: string): void {
    this.validRegistration = false;
    this.errorMessage = errorMessage;
  }

  ngOnDestroy(): void {
    if (this.registrationSubscription != null) {
      this.registrationSubscription.unsubscribe();
    }
  }
}
