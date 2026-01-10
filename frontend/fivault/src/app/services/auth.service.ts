// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';



interface AuthResponse {
  accessToken: string;
  message: string;
  deviceName: string;
  // No refreshToken in response since it's in cookie
}
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly ACCESS_TOKEN_KEY = 'access_token';

  private accessToken: string | null = null;

  constructor(private http: HttpClient) {
    // Load token from localStorage on service initialization
    this.accessToken = localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  login(username: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/auth/login',
      {
        email: username,
        password: password
      })
      .pipe(
        tap(response => {
          this.storeAccessToken(response.accessToken)
        })
      );
  }

  refreshAccessToken(): Observable<AuthResponse> {
    // Cookie is sent automatically by browser
    return this.http.post<AuthResponse>('/auth/refresh', {})
      .pipe(
        tap(response => this.storeAccessToken(response.accessToken))
      );
  }

  logout(): void {
    this.accessToken = null;
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    // Call backend to clear the cookie
    this.http.post('/auth/logout', {}).subscribe();
  }

  // Backend already considers us logged out
  logoutFrontend(): void {
    return;
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  isAuthenticated(): boolean {
    return !!this.accessToken;
  }

  private storeAccessToken(token: string): void {
    this.accessToken = token;
    localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
  }

  tryAuthentication(): Observable<String> {
    return this.http.get<String>('/auth/try-authenticated');
  }

}