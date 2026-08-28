import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
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
  private toastr = inject(ToastrService);

  protected employees = signal<any[]>([]);
  protected isLoading = signal(true);
  protected error = signal<string | null>(null);
  protected showModal = signal(false);
  protected isSubmitting = signal(false);
  protected editingEmployeeId = signal<number | null>(null);

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

  openModal(employee?: any) {
    this.showModal.set(true);

    if (employee) {
      this.editingEmployeeId.set(employee.id);
      this.formData.set({
        firstName: employee.firstName ?? '',
        lastName: employee.lastName ?? '',
        employee_Id: employee.employee_Id ?? ''
      });
      return;
    }

    this.editingEmployeeId.set(null);
    this.formData.set({
      firstName: '',
      lastName: '',
      employee_Id: ''
    });
  }

  closeModal() {
    this.showModal.set(false);
    this.editingEmployeeId.set(null);
    this.formData.set({
      firstName: '',
      lastName: '',
      employee_Id: ''
    });
  }

  saveEmployee() {
    const data = this.formData();
    const employeeId = this.editingEmployeeId();

    if (!data.firstName || !data.lastName || !data.employee_Id) {
      this.toastr.error('Imię, nazwisko i Employee ID są wymagane!');
      return;
    }

    this.isSubmitting.set(true);

    const request = employeeId !== null
      ? this.httpService.updateEmployee(employeeId, data)
      : this.httpService.addEmployee(data);

    request.subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.closeModal();
        this.loadEmployees();
        this.toastr.success(employeeId !== null ? 'Dane pracownika zapisane pomyślnie!' : 'Pracownik dodany pomyślnie!');
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.toastr.error(employeeId !== null ? 'Błąd przy zapisywaniu pracownika' : 'Błąd przy dodawaniu pracownika');
        console.error('Error saving employee:', err);
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
        this.toastr.success('Pracownik usunięty pomyślnie!');
      },
      error: (err) => {
        this.toastr.error('Błąd przy usuwaniu pracownika');
        console.error('Error deleting employee:', err);
      }
    });
  }
}
