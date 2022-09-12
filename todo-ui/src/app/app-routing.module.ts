import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {TodoListComponent} from "./components/todo-list/todo-list.component";
import {AuthRouteGuardService} from "./services/auth-route-guard.service";
import {LoginRouteGuardService} from "./services/login-route-guard.service";
import {RegisterComponent} from "./components/register/register.component";

const routes: Routes = [
  {path: 'register', canActivate: [LoginRouteGuardService], component: RegisterComponent},
  {path: 'login', canActivate: [LoginRouteGuardService], component: LoginComponent},
  {path: '', canActivate: [AuthRouteGuardService], component: TodoListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
