import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('HigienaHarmonogramFrontend');

  protected readonly workSchedules = signal([
    { id: 1, employee: { firstName: 'Jan', lastName: 'Kowalski' }, shift: { name: 'Morning' } },
    { id: 2, employee: { firstName: 'Anna', lastName: 'Nowak' }, shift: { name: 'Afternoon' } },
    { id: 3, employee: { firstName: 'Piotr', lastName: 'Wiśniewski' }, shift: { name: 'Night' } }
  ]);
}
