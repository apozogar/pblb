import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import { RegisterRequest } from '../../models/register-request.model';
import {Router} from "@angular/router";
import {environment} from "../../../enviroments/environment";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private http = inject(HttpClient);
    private router = inject(Router);

    private baseUrl = environment.apiUrl + '/auth';

    login(credentials: { email: string, password: string }): Observable<any> {
        return this.http.post<{ token: string }>(`${this.baseUrl}/login`, credentials).pipe(
            tap(response => {
                if (response.token) {
                    localStorage.setItem('token', response.token);
                }
            })
        );
    }

    register(registerData: RegisterRequest): Observable<any> {
        return this.http.post(`${this.baseUrl}/register`, registerData);
    }

    forgotPassword(email: string): Observable<any> {
        return this.http.post(`${this.baseUrl}/forgot-password`, {email});
    }

    resetPassword(token: string, password: string): Observable<any> {
        return this.http.post(`${this.baseUrl}/reset-password`, {token, password});
    }

    isLoggedIn(): boolean {
        // Aquí también podrías añadir una comprobación de la expiración del token
        return !!localStorage.getItem('token');
    }

    logout(): void {
        localStorage.removeItem('token');
        this.router.navigate(['/auth/login']);
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }
}
