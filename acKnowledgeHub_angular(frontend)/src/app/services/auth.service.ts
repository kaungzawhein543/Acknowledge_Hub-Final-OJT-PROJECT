import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { userInfo } from 'os';
import { catchError, map, Observable, of, Subject, tap } from 'rxjs';
import { ResponseEmail } from '../models/response-email';
import { StaffProfileDTO } from '../models/staff';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth'; // Your backend URL
  private loginSubject = new Subject<void>();
  private logoutSubject = new Subject<void>();
  constructor(private http: HttpClient, private router: Router) { }

  login(staffId: string, password: string,rememberMe : boolean): Observable<HttpResponse<string>> {
    return this.http.post(`${this.apiUrl}/login`, { staffId, password,rememberMe }, { observe: 'response', responseType: 'text', withCredentials: true }).pipe(
      tap(() =>{
        this.loginSubject.next();
      })
    );
  }

  // Emits when the user logs in successfully 
  onLogin(): Observable<void> { 
    return this.loginSubject.asObservable(); 
  } 
 
  // Emits when the user logs out 
  onLogout(): Observable<void> { 
    return this.logoutSubject.asObservable(); 
  }


  changePassword(staffId: string, oldPassword: string, newPassword: string): Observable<string> {
    const payload = {
      staffId: staffId,
      oldPassword: oldPassword,
      newPassword: newPassword
    };
    return this.http.post(`${this.apiUrl}/changePassword`, payload, { responseType: 'text', withCredentials: true });
  }

  getUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`, { withCredentials: true });
  }

  logout(): Observable<string> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { responseType: 'text', withCredentials: true }).pipe(
      tap(()=>{
        if (typeof window !== 'undefined') { // Check if window is defined
          localStorage.clear();
        }
        this.loginSubject.next();
      })
    );
  }

  getUserStatus(): Observable<{ isLoggedIn: boolean, userInfo?: any }> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.get<any>(this.apiUrl, { headers: headers, withCredentials: true }).pipe(
      map(data => ({
        isLoggedIn: true,
        userInfo: data
      })),
      catchError(error => of({ isLoggedIn: false })) // Handle errors (e.g., unauthorized or server errors)
    );
  }

  isLoggedIn(): Observable<boolean> {
    return this.http.get<any>(`${this.apiUrl}/me`, { withCredentials: true })
      .pipe(
        map(response => {
          console.log(response);
          if (response.isLoggedIn && response.user.id) { 
            if (typeof window !== 'undefined') { 
              localStorage.setItem('id', response.user.id.toString()); 
            }
          }
          return response.isLoggedIn;
        }),
        catchError(error => {
          console.error('Error during authentication check:', error);
          return of(false);
        })
      );
  }


  hasRole(expectedRole: string): Observable<boolean> {
    return this.getUserInfo().pipe(
      map(userInfo => {
        return userInfo.user.role === expectedRole;
      }),
      catchError(() => of(false))
    );
  }

  hasPostion(expectedPosition: string): Observable<boolean> {
    return this.getUserInfo().pipe(
      map(userInfo => userInfo?.position === expectedPosition),
      catchError(() => of(false))
    );
  }

  // Method to get user information including roles
  getUserInfo(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/me`, { withCredentials: true }).pipe(
      catchError(() => of(null))  // In case of error, return null
    );
  }

  getProfile(): Observable<StaffProfileDTO> {
    return this.http.get<StaffProfileDTO>(`${this.apiUrl}/profile`, { withCredentials: true }).pipe(
      catchError(error => {
        console.error('Error fetching profile:', error);
        return of(null as unknown as StaffProfileDTO); // Handle the error and return null in case of failure
      })
    );
  }

  

  getOTP(staffId: string): Observable<ResponseEmail> {
    return this.http.post<ResponseEmail>(`http://localhost:8080/api/v1/email/send-otp?staffId=${staffId}`, {}, { withCredentials: true })
  }

  sendOTP(email: string, otp: string): Observable<any> {
    return this.http.post(`http://localhost:8080/api/v1/email/verify-otp`, { email, otp }, { withCredentials: true })
  }

  addPassword(email: string, password: string): Observable<any> {
    return this.http.post(`http://localhost:8080/api/v1/email/update-password`, { email, password }, { withCredentials: true })
  }

}