import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {LoginComponent} from './components/login/login.component';
import {FormsModule} from '@angular/forms';
import {DashboardComponent} from './components/dashboard/dashboard.component';
import {TodoComponent} from './components/todo/todo.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [AppComponent, LoginComponent, DashboardComponent, TodoComponent],
  imports: [BrowserModule, FormsModule, AppRoutingModule, NgbModule, FontAwesomeModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {
}
