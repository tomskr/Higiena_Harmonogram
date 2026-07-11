import { Routes } from '@angular/router';
import { EmployeesListComponent } from './employees-list/employees-list';
import { HomeComponent } from './home/home';
import { EmployeeDetailComponent } from './employee-detail/employee-detail';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'employees', component: EmployeesListComponent },
  { path: 'employee/:id', component: EmployeeDetailComponent }
];
