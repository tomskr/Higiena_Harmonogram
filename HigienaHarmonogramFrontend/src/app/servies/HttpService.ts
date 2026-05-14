import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class HttpService {
  private readonly apiUrl = '/api/employees';


  private http = inject(HttpClient);

  getEmployees(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
