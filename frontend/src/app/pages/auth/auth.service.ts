import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, of, switchMap, tap} from 'rxjs';
import {RegisterRequest} from '@/models/register-request.model';
import {Router} from '@angular/router';
import {environment} from "../../../environments/environment";
import {jwtDecode} from "jwt-decode";
import {User} from "@/interfaces/user";
import {PenaService} from "@/services/pena.service";
import {Pena} from "@/interfaces/socio.interface";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private http = inject(HttpClient);
    private router = inject(Router);
    private penaService = inject(PenaService);

    private baseUrl = environment.apiUrl + '/auth';

    private currentUserSubject = new BehaviorSubject<User | null>(null);
    public currentUser = this.currentUserSubject.asObservable();
    private currentPenaSubject = new BehaviorSubject<Pena | null>(null);
    public currentPena = this.currentPenaSubject.asObservable();

    constructor() {
        const token = this.getToken();
        if (token) {
            this.decodeToken(token);
            this.loadPenaFromStorage();
        }
    }

    login(credentials: { email: string, password: string }): Observable<any> {
        return this.http.post<{ token: string }>(`${this.baseUrl}/login`, credentials).pipe(
            switchMap(response => {
                if (!response.token) {
                    return of(response); // Continuar el flujo si no hay token
                }

                localStorage.setItem('token', response.token);
                this.decodeToken(response.token);

                const user = this.currentUserSubject.getValue();
                const clubId = (user as any)?.clubId;

                if (!clubId) {
                    return of(response); // No hay clubId, continuar
                }

                return this.penaService.get(clubId.toString()).pipe(
                    tap(penaResponse => {
                        const pena = penaResponse.data;
                        this.currentPenaSubject.next(pena);
                        localStorage.setItem('currentPena', JSON.stringify(pena));
                    }),
                    switchMap(() => of(response)) // Devolver la respuesta original del login
                );
            })
        );
    }

    register(registerData
             :
             RegisterRequest
    ):
        Observable<any> {
        return this.http.post(`${this.baseUrl}/register`, registerData);
    }

    loginAfterRegister(registerData
                       :
                       RegisterRequest
    ):
        Observable<any> {
        return this.register(registerData).pipe(
            switchMap(() => {
                return this.login({email: registerData.email, password: registerData.password});
            })
        );
    }

    forgotPassword(email
                   :
                   string
    ):
        Observable<any> {
        return this.http.post(`${this.baseUrl}/forgot-password`, {email});
    }

    resetPassword(token: string, password: string
    ):
        Observable<any> {
        return this.http.post(`${this.baseUrl}/reset-password`, {token, password});
    }

    isLoggedIn()
        :
        boolean {
        // Aquí también podrías añadir una comprobación de la expiración del token
        return !!localStorage.getItem('token');
    }

    logout()
        :
        void {
        localStorage.removeItem('token');
        localStorage.removeItem('currentPena');
        this.currentUserSubject.next(null);
        this.router.navigate(['/auth/login']);
    }

    getToken()
        :
        string | null {
        return localStorage.getItem('token');
    }

    decodeToken(token
                :
                string
    ):
        void {
        try {
            const decodedToken
                :
                User = jwtDecode(token);
            this.currentUserSubject.next(decodedToken);
        } catch
            (error) {
            this.currentUserSubject.next(null);
        }
    }

    private loadPenaFromStorage(): void {
        const penaData = localStorage.getItem('currentPena');
        if (penaData) {
            try {
                const pena: Pena = JSON.parse(penaData);
                this.currentPenaSubject.next(pena);
            } catch (error) {
                console.error('Error al parsear la peña desde localStorage:', error);
            }
        }
    }
}
