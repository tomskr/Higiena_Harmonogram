import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
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

  protected employee = signal<any>(null);
  protected employeeShifts = signal<any[]>([]);
  protected isLoading = signal(true);
  protected error = signal<string | null>(null);
  protected currentDate = signal(new Date());
  protected showShiftModal = signal(false);
  protected selectedDate = signal<Date | null>(null);
  protected isSubmittingShift = signal(false);

  protected shiftFormData = signal({
    shiftType: '',
    shiftLength: '',
    isHoliday: false
  });

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

  openShiftModal(day: number) {
    if (day === 0) return;

    const date = new Date(this.currentDate().getFullYear(), this.currentDate().getMonth(), day);
    this.selectedDate.set(date);

    const isHoliday = this.isSunday(day);
    this.shiftFormData.set({
      shiftType: '',
      shiftLength: '',
      isHoliday: isHoliday
    });

    this.showShiftModal.set(true);
  }

  closeShiftModal() {
    this.showShiftModal.set(false);
    this.selectedDate.set(null);
  }

  private formatLocalDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  addShift() {
    const formData = this.shiftFormData();
    const empId = this.employee()?.id;

    if (!formData.shiftType || !empId || !this.selectedDate()) {
      alert('Typ zmiany jest wymagany!');
      return;
    }

    if (!formData.shiftLength || !empId || !this.selectedDate()) {
      alert('Dłuugość zmiany jest wymagana!');
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

    this.httpService.addShift(shiftData).subscribe({
      next: () => {
        this.isSubmittingShift.set(false);
        this.closeShiftModal();
        alert('Zmiana dodana pomyślnie!');
      },
      error: (err) => {
        this.isSubmittingShift.set(false);
        alert('Błąd przy dodawaniu zmiany');
        console.error('Error adding shift:', err);
      }
    });
  }
}
