import { Component } from '@angular/core';
import { signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent {
  protected readonly title = signal('HigienaHarmonogramFrontend');

  protected readonly workSchedules = signal([
    { id: 1, employee: { firstName: 'Jan', lastName: 'Kowalski' }, shift: { name: 'u8' } },
    { id: 2, employee: { firstName: 'Anna', lastName: 'Nowak' }, shift: { name: 'u11' } },
    { id: 3, employee: { firstName: 'Piotr', lastName: 'Wiśniewski' }, shift: { name: 'u8' } }
  ]);
}
