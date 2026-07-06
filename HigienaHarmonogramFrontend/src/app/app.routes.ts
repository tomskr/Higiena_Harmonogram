import { Routes } from '@angular/router';
import { EmployeesListComponent } from './employees-list/employees-list';
import { HomeComponent } from './home/home';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'employees', component: EmployeesListComponent }
];
