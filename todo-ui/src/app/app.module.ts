import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {LoginComponent} from './components/login/login.component';
import {FormsModule} from '@angular/forms';
import {TodoDetailComponent} from './components/todo-list/todo-detail/todo-detail.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {TodoEditComponent} from './components/todo-list/todo-edit/todo-edit.component';
import {TodoListComponent} from './components/todo-list/todo-list.component';
import {AuthInterceptor} from "./interceptors/auth-interceptor.service";
import { RegisterComponent } from './components/register/register.component';

@NgModule({
  declarations: [AppComponent, LoginComponent, TodoDetailComponent, TodoEditComponent, TodoListComponent, RegisterComponent],
  imports: [BrowserModule, FormsModule, HttpClientModule, AppRoutingModule, NgbModule, FontAwesomeModule, HttpClientModule],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
