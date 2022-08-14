import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {TodoListComponent} from "./components/todo-list/todo-list.component";

const routes: Routes = [
    {path: 'login', component: LoginComponent},
    {path: 'list', component: TodoListComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
