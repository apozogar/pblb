import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, switchMap, tap} from 'rxjs';
import {RegisterRequest} from '@/models/register-request.model';
import {Router} from '@angular/router';
import {environment} from "../../../enviroments/environment";
import { jwtDecode } from "jwt-decode";
import {User} from "@/interfaces/user";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private http = inject(HttpClient);
    private router = inject(Router);

    private baseUrl = environment.apiUrl + '/auth';

    private currentUserSubject = new BehaviorSubject<User | null>(null);
    public currentUser = this.currentUserSubject.asObservable();

    constructor() {
        const token = this.getToken();
        if (token) {
            this.decodeToken(token);
        }
    }

    login(credentials: { email: string, password: string }): Observable<any> {
        return this.http.post<{ token: string }>(`${this.baseUrl}/login`, credentials).pipe(
            tap(response => {
                if (response.token) {
                    localStorage.setItem('token', response.token);
                    this.decodeToken(response.token);
                }
            })
        );
    }

    register(registerData: RegisterRequest): Observable<any> {
        return this.http.post(`${this.baseUrl}/register`, registerData);
    }

    loginAfterRegister(registerData: RegisterRequest): Observable<any> {
        return this.register(registerData).pipe(
            switchMap(() => {
                return this.login({email: registerData.email, password: registerData.password});
            })
        );
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
        this.currentUserSubject.next(null);
        this.router.navigate(['/auth/login']);
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }

    private decodeToken(token: string): void {
        try {
            const decodedToken: User = jwtDecode(token);
            this.currentUserSubject.next(decodedToken);
        } catch (error) {
            this.currentUserSubject.next(null);
        }
    }
}
