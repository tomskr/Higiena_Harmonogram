import {Component, inject, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import {HttpService} from './servies/HttpService';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  standalone: true,
  styleUrl: './app.css'
})
export class App {
  private HttpService = inject(HttpService);

  protected readonly employees = signal<any[]>([]);

  constructor() {
    console.log('App constructor');
    this.HttpService.getEmployees().subscribe(employees => {
      this.employees.set(employees);
    })
  }

  protected readonly title = signal('HigienaHarmonogramFrontend');

  protected readonly workSchedules = signal([
    { id: 1, employee: { firstName: 'Jan', lastName: 'Kowalski' }, shift: { name: 'u8' } },
    { id: 2, employee: { firstName: 'Anna', lastName: 'Nowak' }, shift: { name: 'u11' } },
    { id: 3, employee: { firstName: 'Piotr', lastName: 'Wiśniewski' }, shift: { name: 'u8' } }
  ]);
}
