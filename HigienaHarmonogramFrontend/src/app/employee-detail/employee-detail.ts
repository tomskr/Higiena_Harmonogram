import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HttpService } from '../servies/HttpService';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './employee-detail.html',
  styleUrl: './employee-detail.css'
})
export class EmployeeDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private httpService = inject(HttpService);
  private toastr = inject(ToastrService);

  protected employee = signal<any>(null);
  protected employeeShifts = signal<any[]>([]);
  protected isLoading = signal(true);
  protected error = signal<string | null>(null);
  protected currentDate = signal(new Date());
  protected showShiftModal = signal(false);
  protected selectedDate = signal<Date | null>(null);
  protected selectedShiftId = signal<number | null>(null);
  protected isSubmittingShift = signal(false);
  protected showEditEmployeeModal = signal(false);
  protected isSubmittingEmployee = signal(false);
  protected employeeFormData = signal({
    firstName: '',
    lastName: '',
    employee_Id: '',
    photo: ''
  });
  protected employeePhotoPreview = signal<string>('');
  protected selectedEmployeePhotoName = signal<string>('Nie wybrano pliku');

  protected shiftFormData = signal({
    shiftType: '',
    shiftLength: '',
    isHoliday: false
  });

  protected get isEditMode(): boolean {
    return this.selectedShiftId() !== null;
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadEmployee(parseInt(id));
      }
    });
  }

  private loadEmployee(id: number) {
    this.httpService.getEmployeeById(id).subscribe({
      next: (data) => {
        this.employee.set(data);
        this.loadEmployeeShifts();
      },
      error: (err) => {
        this.error.set('Błąd przy ładowaniu danych pracownika');
        this.isLoading.set(false);
        console.error('Error fetching employee:', err);
      }
    });
  }

  editEmployee() {
    const employee = this.employee();
    if (!employee) {
      return;
    }

    this.showEditEmployeeModal.set(true);
    this.employeeFormData.set({
      firstName: employee.firstName ?? '',
      lastName: employee.lastName ?? '',
      employee_Id: employee.employee_Id ?? '',
      photo: employee.photo ?? ''
    });
    this.employeePhotoPreview.set(employee.photo ?? '');
    this.selectedEmployeePhotoName.set(employee.photo ? 'Zdjęcie zapisane' : 'Nie wybrano pliku');
  }

  closeEditEmployeeModal() {
    this.showEditEmployeeModal.set(false);
    this.isSubmittingEmployee.set(false);
    this.employeeFormData.set({
      firstName: '',
      lastName: '',
      employee_Id: '',
      photo: ''
    });
    this.employeePhotoPreview.set('');
    this.selectedEmployeePhotoName.set('Nie wybrano pliku');
  }

  onEmployeePhotoSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      this.selectedEmployeePhotoName.set('Nie wybrano pliku');
      return;
    }

    if (!file.type.startsWith('image/')) {
      this.toastr.error('Wybierz poprawny plik graficzny.');
      this.selectedEmployeePhotoName.set('Nie wybrano pliku');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const result = typeof reader.result === 'string' ? reader.result : '';
      this.employeeFormData.update((current) => ({ ...current, photo: result }));
      this.employeePhotoPreview.set(result);
      this.selectedEmployeePhotoName.set(file.name);
    };
    reader.readAsDataURL(file);
  }

  saveEmployeeEdit() {
    const employee = this.employee();
    const data = { ...this.employeeFormData() };

    if (!employee) {
      return;
    }

    if (!data.firstName || !data.lastName || !data.employee_Id) {
      this.toastr.error('Imię, nazwisko i Employee ID są wymagane!');
      return;
    }

    if (!data.photo) {
      delete (data as { photo?: string }).photo;
    }

    this.isSubmittingEmployee.set(true);

    this.httpService.updateEmployee(employee.id, data).subscribe({
      next: () => {
        this.isSubmittingEmployee.set(false);
        this.closeEditEmployeeModal();
        this.loadEmployee(employee.id);
        this.toastr.success('Dane pracownika zapisane pomyślnie!');
      },
      error: (err) => {
        this.isSubmittingEmployee.set(false);
        this.toastr.error('Błąd przy zapisywaniu pracownika');
        console.error('Error updating employee:', err);
      }
    });
  }

  private loadEmployeeShifts() {
    const employeeId = this.employee()?.id;
    if (!employeeId) {
      this.isLoading.set(false);
      return;
    }

    this.httpService.getShifts().subscribe({
      next: (shifts) => {
        this.employeeShifts.set(
          shifts.filter((shift) => Number(shift?.employee?.id ?? shift?.employeeId) === Number(employeeId))
        );
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading employee shifts:', err);
        this.isLoading.set(false);
      }
    });
  }

  get daysInMonth(): number[] {
    const year = this.currentDate().getFullYear();
    const month = this.currentDate().getMonth();
    const firstDayOfWeek = new Date(year, month, 1).getDay();
    // Konwertuj getDay() (0=niedziela) na indeks gdzie poniedziałek=0, niedziela=6
    const adjustedFirstDay = firstDayOfWeek === 0 ? 6 : firstDayOfWeek - 1;
    const daysCount = new Date(year, month + 1, 0).getDate();

    const days: number[] = [];
    // Dodaj puste dni na początku (do poniedziałku)
    for (let i = 0; i < adjustedFirstDay; i++) {
      days.push(0);
    }
    // Dodaj dni miesiąca
    for (let i = 1; i <= daysCount; i++) {
      days.push(i);
    }
    return days;
  }

  get monthYear(): string {
    const months = ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec',
                    'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'];
    const year = this.currentDate().getFullYear();
    const month = months[this.currentDate().getMonth()];
    return `${month} ${year}`;
  }

  previousMonth() {
    const date = new Date(this.currentDate());
    date.setMonth(date.getMonth() - 1);
    this.currentDate.set(date);
  }

  nextMonth() {
    const date = new Date(this.currentDate());
    date.setMonth(date.getMonth() + 1);
    this.currentDate.set(date);
  }

  isToday(day: number): boolean {
    if (day === 0) return false;
    const today = new Date();
    return day === today.getDate() &&
           this.currentDate().getMonth() === today.getMonth() &&
           this.currentDate().getFullYear() === today.getFullYear();
  }

  isSunday(day: number): boolean {
    if (day === 0) return false;
    const date = new Date(this.currentDate().getFullYear(), this.currentDate().getMonth(), day);
    return date.getDay() === 0;
  }

  isHolidayOnDay(day: number): boolean {
    if (day === 0) return false;

    const targetDate = new Date(
      this.currentDate().getFullYear(),
      this.currentDate().getMonth(),
      day
    );

    const targetKey = this.formatDateKey(targetDate);
    const shift = this.employeeShifts().find((item) => {
      const shiftDate = item?.fullDate ? String(item.fullDate).substring(0, 10) : null;
      return shiftDate === targetKey;
    });

    return Boolean(shift?.isHoliday) || this.isSunday(day);
  }

  private formatDateKey(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  hasShiftOnDay(day: number): boolean {
    if (day === 0) return false;

    const targetDate = new Date(
      this.currentDate().getFullYear(),
      this.currentDate().getMonth(),
      day
    );

    const targetKey = this.formatDateKey(targetDate);

    return this.employeeShifts().some((shift) => {
      const shiftDate = shift?.fullDate ? String(shift.fullDate).substring(0, 10) : null;
      return shiftDate === targetKey;
    });
  }

  getShiftTypeOnDay(day: number): string {
    if (day === 0) return '';

    const targetDate = new Date(
      this.currentDate().getFullYear(),
      this.currentDate().getMonth(),
      day
    );

    const targetKey = this.formatDateKey(targetDate);

    const shift = this.employeeShifts().find((item) => {
      const shiftDate = item?.fullDate ? String(item.fullDate).substring(0, 10) : null;
      return shiftDate === targetKey;
    });

    return shift?.shiftType ?? '';
  }

  private getShiftForSelectedDate(date: Date): any | undefined {
    const targetKey = this.formatDateKey(date);

    return this.employeeShifts().find((shift) => {
      const shiftDate = shift?.fullDate ? String(shift.fullDate).substring(0, 10) : null;
      return shiftDate === targetKey;
    });
  }

  openShiftModal(day: number) {
    if (day === 0) return;

    const date = new Date(this.currentDate().getFullYear(), this.currentDate().getMonth(), day);
    this.selectedDate.set(date);

    const existingShift = this.getShiftForSelectedDate(date);
    this.selectedShiftId.set(existingShift?.id ?? null);

    const isHoliday = existingShift?.isHoliday ?? this.isSunday(day);
    this.shiftFormData.set({
      shiftType: existingShift ? String(existingShift.shiftType ?? '') : '',
      shiftLength: existingShift ? String(existingShift.shiftLength ?? '') : '',
      isHoliday: Boolean(isHoliday)
    });

    this.showShiftModal.set(true);
  }

  closeShiftModal() {
    this.showShiftModal.set(false);
    this.selectedDate.set(null);
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
    const empId = this.employee()?.id;
    const shiftId = this.selectedShiftId();

    if (!formData.shiftType || !empId || !this.selectedDate()) {
      this.toastr.error('Typ zmiany jest wymagany!');
      return;
    }

    if (!formData.shiftLength || !empId || !this.selectedDate()) {
      this.toastr.error('Długość zmiany jest wymagana!');
      return;
    }

    this.isSubmittingShift.set(true);

    const shiftData = {
      shiftType: formData.shiftType,
      shiftLength: Number(formData.shiftLength),
      isHoliday: formData.isHoliday,
      fullDate: this.formatLocalDate(this.selectedDate()!),
      employee: { id: empId }
    };

    const request = shiftId !== null
      ? this.httpService.updateShift(shiftId, shiftData)
      : this.httpService.addShift(shiftData);

    request.subscribe({
      next: () => {
        this.isSubmittingShift.set(false);
        this.loadEmployeeShifts();
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
}
