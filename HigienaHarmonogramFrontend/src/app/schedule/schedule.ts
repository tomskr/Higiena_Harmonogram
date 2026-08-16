import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpService } from '../servies/HttpService';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './schedule.html',
  styleUrl: './schedule.css'
})
export class ScheduleComponent implements OnInit {
  private httpService = inject(HttpService);

  protected employees = signal<any[]>([]);
  protected shifts = signal<any[]>([]);
  protected currentDate = signal(new Date());
  protected isLoading = signal(true);
  protected error = signal<string | null>(null);

  protected weekDays = ['Pn', 'Wt', 'Śr', 'Cz', 'Pt', 'Sb', 'Nd'];

  ngOnInit() {
    this.loadScheduleData();
  }

  private loadScheduleData() {
    this.httpService.getEmployees().subscribe({
      next: (employees) => {
        this.employees.set(employees);

        this.httpService.getShifts().subscribe({
          next: (shifts) => {
            this.shifts.set(shifts);
            this.isLoading.set(false);
          },
          error: (err) => {
            this.error.set('Błąd przy ładowaniu zmian w harmonogramie');
            this.isLoading.set(false);
            console.error('Error loading shifts:', err);
          }
        });
      },
      error: (err) => {
        this.error.set('Błąd przy ładowaniu harmonogramu');
        this.isLoading.set(false);
        console.error('Error loading schedule:', err);
      }
    });
  }

  get monthYear(): string {
    const months = ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 
                    'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'];
    const year = this.currentDate().getFullYear();
    const month = months[this.currentDate().getMonth()];
    return `${month} ${year}`;
  }

  get currentWeekDates(): Date[] {
    const date = new Date(this.currentDate());
    const day = date.getDay();
    // Konwertuj na poniedziałek (0 = poniedziałek w naszym systemie)
    const adjustedDay = day === 0 ? 6 : day - 1;
    const diff = date.getDate() - adjustedDay;
    
    const mondayDate = new Date(date.setDate(diff));
    const dates: Date[] = [];
    
    for (let i = 0; i < 7; i++) {
      const newDate = new Date(mondayDate);
      newDate.setDate(mondayDate.getDate() + i);
      dates.push(newDate);
    }
    
    return dates;
  }

  private formatDateKey(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private normalizeShiftDate(value: string | null | undefined): string | null {
    if (!value) return null;
    if (typeof value === 'string' && value.length >= 10) {
      return value.substring(0, 10);
    }
    return null;
  }

  getShiftForDay(employeeId: number, date: Date): any | null {
    const dateKey = this.formatDateKey(date);

    return this.shifts().find((shift) => {
      const shiftEmployeeId = shift?.employee?.id ?? shift?.employeeId;
      const shiftDate = this.normalizeShiftDate(shift?.fullDate);
      return Number(shiftEmployeeId) === Number(employeeId) && shiftDate === dateKey;
    }) ?? null;
  }

  previousWeek() {
    const date = new Date(this.currentDate());
    date.setDate(date.getDate() - 7);
    this.currentDate.set(date);
  }

  nextWeek() {
    const date = new Date(this.currentDate());
    date.setDate(date.getDate() + 7);
    this.currentDate.set(date);
  }

  formatDate(date: Date): string {
    return `${date.getDate()}/${date.getMonth() + 1}`;
  }
}
