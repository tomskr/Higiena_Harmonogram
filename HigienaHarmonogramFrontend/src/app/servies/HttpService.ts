import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class HttpService {
  private readonly apiUrl = '/api/employees';
  private readonly shiftsUrl = '/api/shifts';

  private http = inject(HttpClient);

  getEmployees(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getEmployeeById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  addEmployee(employee: any): Observable<any> {
    return this.http.post(this.apiUrl, employee);
  }

  deleteEmployee(employeeId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${employeeId}`);
  }

  addShift(shift: any): Observable<any> {
    return this.http.post(this.shiftsUrl, shift);
  }

  updateShift(shift: any): Observable<any> {
    return this.http.put(this.shiftsUrl, shift);
  }
}
