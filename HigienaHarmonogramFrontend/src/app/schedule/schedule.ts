import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HttpService } from '../servies/HttpService';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './schedule.html',
  styleUrl: './schedule.css'
})
export class ScheduleComponent implements OnInit {
  private httpService = inject(HttpService);
  private toastr = inject(ToastrService);

  protected employees = signal<any[]>([]);
  protected shifts = signal<any[]>([]);
  protected currentDate = signal(new Date());
  protected isLoading = signal(true);
  protected error = signal<string | null>(null);
  protected showShiftModal = signal(false);
  protected selectedDate = signal<Date | null>(null);
  protected selectedEmployeeId = signal<number | null>(null);
  protected selectedShiftId = signal<number | null>(null);
  protected isSubmittingShift = signal(false);

  protected shiftFormData = signal({
    shiftType: '',
    shiftLength: '',
    isHoliday: false
  });

  protected weekDays = ['Pn', 'Wt', 'Śr', 'Cz', 'Pt', 'Sb', 'Nd'];

  protected get isEditMode(): boolean {
    return this.selectedShiftId() !== null;
  }

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

  private getShiftForEmployeeAndDate(employeeId: number, date: Date): any | undefined {
    const dateKey = this.formatDateKey(date);

    return this.shifts().find((shift) => {
      const shiftEmployeeId = shift?.employee?.id ?? shift?.employeeId;
      const shiftDate = this.normalizeShiftDate(shift?.fullDate);
      return Number(shiftEmployeeId) === Number(employeeId) && shiftDate === dateKey;
    });
  }

  getShiftForDay(employeeId: number, date: Date): any | null {
    return this.getShiftForEmployeeAndDate(employeeId, date) ?? null;
  }

  openShiftModal(employeeId: number, day: Date) {
    const date = new Date(day);
    this.selectedEmployeeId.set(employeeId);
    this.selectedDate.set(date);

    const existingShift = this.getShiftForEmployeeAndDate(employeeId, date);
    this.selectedShiftId.set(existingShift?.id ?? null);

    this.shiftFormData.set({
      shiftType: existingShift ? String(existingShift.shiftType ?? '') : '',
      shiftLength: existingShift ? String(existingShift.shiftLength ?? '') : '',
      isHoliday: Boolean(existingShift?.isHoliday ?? false)
    });

    this.showShiftModal.set(true);
  }

  closeShiftModal() {
    this.showShiftModal.set(false);
    this.selectedDate.set(null);
    this.selectedEmployeeId.set(null);
    this.selectedShiftId.set(null);
    this.shiftFormData.set({
      shiftType: '',
      shiftLength: '',
      isHoliday: false
    });
  }

  private formatLocalDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  saveShift() {
    const formData = this.shiftFormData();
    const employeeId = this.selectedEmployeeId();
    const shiftId = this.selectedShiftId();
    const selectedDate = this.selectedDate();

    if (!formData.shiftType || !employeeId || !selectedDate) {
      this.toastr.error('Typ zmiany, pracownik i data są wymagane!');
      return;
    }

    if (!formData.shiftLength) {
      this.toastr.error('Długość zmiany jest wymagana!');
      return;
    }

    this.isSubmittingShift.set(true);

    const shiftData = {
      shiftType: formData.shiftType,
      shiftLength: Number(formData.shiftLength),
      isHoliday: formData.isHoliday,
      fullDate: this.formatLocalDate(selectedDate),
      employee: { id: employeeId }
    };

    const request = shiftId !== null
      ? this.httpService.updateShift(shiftId, shiftData)
      : this.httpService.addShift(shiftData);

    request.subscribe({
      next: () => {
        this.isSubmittingShift.set(false);
        this.loadScheduleData();
        this.closeShiftModal();
        this.toastr.success(shiftId !== null ? 'Zmiany zapisane pomyślnie!' : 'Zmiana dodana pomyślnie!');
      },
      error: (err) => {
        this.isSubmittingShift.set(false);
        this.toastr.error(shiftId !== null ? 'Błąd przy zapisywaniu zmiany' : 'Błąd przy dodawaniu zmiany');
        console.error('Error saving shift:', err);
      }
    });
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
