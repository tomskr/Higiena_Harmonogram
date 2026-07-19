import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpService } from '../servies/HttpService';

@Component({
  selector: 'app-employees-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './employees-list.html',
  styleUrl: './employees-list.css'
})
export class EmployeesListComponent implements OnInit {
  private httpService = inject(HttpService);

  protected employees = signal<any[]>([]);
  protected isLoading = signal(true);
  protected error = signal<string | null>(null);
  protected showModal = signal(false);
  protected isSubmitting = signal(false);
  
  protected formData = signal({
    firstName: '',
    lastName: '',
    employee_Id: ''
  });

  ngOnInit() {
    this.loadEmployees();
  }

  private loadEmployees() {
    this.httpService.getEmployees().subscribe({
      next: (data) => {
        this.employees.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set('Błąd przy ładowaniu pracowników');
        this.isLoading.set(false);
        console.error('Error fetching employees:', err);
      }
    });
  }

  openModal() {
    this.showModal.set(true);
    this.formData.set({
      firstName: '',
      lastName: '',
      employee_Id: ''
    });
  }

  closeModal() {
    this.showModal.set(false);
  }

  addEmployee() {
    const data = this.formData();
    
    if (!data.firstName || !data.lastName || !data.employee_Id) {
      alert('Imię, nazwisko i Employee ID są wymagane!');
      return;
    }

    this.isSubmitting.set(true);
    
    this.httpService.addEmployee(data).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.closeModal();
        this.loadEmployees();
        alert('Pracownik dodany pomyślnie!');
      },
      error: (err) => {
        this.isSubmitting.set(false);
        alert('Błąd przy dodawaniu pracownika');
        console.error('Error adding employee:', err);
      }
    });
  }

  deleteEmployee(employeeId: number) {
    const confirmed = confirm('Czy na pewno chcesz usunąć tego pracownika?');
    
    if (!confirmed) {
      return;
    }

    this.httpService.deleteEmployee(employeeId).subscribe({
      next: () => {
        this.loadEmployees();
        alert('Pracownik usunięty pomyślnie!');
      },
      error: (err) => {
        alert('Błąd przy usuwaniu pracownika');
        console.error('Error deleting employee:', err);
      }
    });
  }
}
