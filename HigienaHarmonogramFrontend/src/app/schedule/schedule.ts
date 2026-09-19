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

  // Multi-day add UI state (frontend-only, no backend calls)
  protected showMultiDayModal = signal(false);
  protected multiSelectedDates = signal<string[]>([]);
  protected multiSelectionMode = signal<'week' | 'month' | 'range'>('week');
  protected multiVisibleMonth = signal<Date>(new Date(this.currentDate()));
  protected multiShiftFormData = signal({ shiftType: '', shiftLength: '', isHoliday: false });

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

  /* Multi-day modal helpers */
  startMultiDayAdd(employeeId: number) {
    this.selectedEmployeeId.set(employeeId);
    // default to current month view and select current week
    this.multiVisibleMonth.set(new Date(this.currentDate()));
    const weekDates = this.currentWeekDates.map(d => this.formatLocalDate(d));
    this.multiSelectedDates.set(weekDates);
    this.multiSelectionMode.set('week');
    this.multiShiftFormData.set({ shiftType: '', shiftLength: '', isHoliday: false });
    this.showMultiDayModal.set(true);
  }

  closeMultiDayModal() {
    this.showMultiDayModal.set(false);
    this.multiSelectedDates.set([]);
  }

  private dateKey(date: Date): string {
    return this.formatLocalDate(date);
  }

  getMonthGrid(monthDate: Date): (Date | null)[] {
    const year = monthDate.getFullYear();
    const month = monthDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const firstWeekday = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1; // Monday=0

    const cells: (Date | null)[] = [];
    for (let i = 0; i < firstWeekday; i++) cells.push(null);

    for (let d = 1; d <= lastDay.getDate(); d++) {
      cells.push(new Date(year, month, d));
    }

    // pad to full weeks
    while (cells.length % 7 !== 0) cells.push(null);
    return cells;
  }

  toggleMultiDate(date: Date) {
    const key = this.dateKey(date);
    const list = [...this.multiSelectedDates()];
    const idx = list.indexOf(key);
    if (idx >= 0) list.splice(idx, 1);
    else list.push(key);
    this.multiSelectedDates.set(list.sort());
  }

  isMultiDateSelected(date: Date): boolean {
    return this.multiSelectedDates().includes(this.dateKey(date));
  }

  selectMultiMonth() {
    const month = this.multiVisibleMonth();
    const year = month.getFullYear();
    const last = new Date(year, month.getMonth() + 1, 0).getDate();
    const arr: string[] = [];
    for (let d = 1; d <= last; d++) {
      arr.push(this.formatLocalDate(new Date(year, month.getMonth(), d)));
    }
    this.multiSelectedDates.set(arr);
    this.multiSelectionMode.set('month');
  }

  selectMultiWeek(weekStartDate: Date) {
    const start = new Date(weekStartDate);
    const arr: string[] = [];
    for (let i = 0; i < 7; i++) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      arr.push(this.formatLocalDate(d));
    }
    this.multiSelectedDates.set(arr);
    this.multiSelectionMode.set('week');
  }

  prevMultiMonth() {
    const d = new Date(this.multiVisibleMonth());
    d.setMonth(d.getMonth() - 1);
    this.multiVisibleMonth.set(d);
    this.multiSelectedDates.set([]);
  }

  nextMultiMonth() {
    const d = new Date(this.multiVisibleMonth());
    d.setMonth(d.getMonth() + 1);
    this.multiVisibleMonth.set(d);
    this.multiSelectedDates.set([]);
  }

  saveMultiDayShifts() {
    const empId = this.selectedEmployeeId();
    if (!empId) {
      this.toastr.error('Nie wybrano pracownika');
      return;
    }

    const form = this.multiShiftFormData();
    if (!form.shiftType || !form.shiftLength) {
      this.toastr.error('Typ i długość zmiany są wymagane');
      return;
    }

    const existing = [...this.shifts()];
    const toAdd = this.multiSelectedDates().map(dStr => ({
      id: Math.floor(Math.random() * 1000000) + Date.now(),
      shiftType: form.shiftType,
      shiftLength: Number(form.shiftLength),
      isHoliday: Boolean(form.isHoliday),
      fullDate: dStr,
      employee: { id: empId }
    }));

    // Update local state only (no backend call as requested)
    this.shifts.set([...existing, ...toAdd]);
    this.toastr.success(`Dodano ${toAdd.length} zmian(y) (lokalnie)`);
    this.closeMultiDayModal();
  }
}
