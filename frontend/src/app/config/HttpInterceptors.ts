import {Injectable} from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor,
    HttpErrorResponse
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {AuthService} from "@/pages/auth/auth.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        const token = this.authService.getToken();
        let requestToHandle = request;

        // 1. Clonar la solicitud y agregar el token si existe
        if (token) {
            requestToHandle = request.clone({
                headers: request.headers.set('Authorization', `Bearer ${token}`)
            });
        }

        // 2. Manejar la solicitud y usar catchError en el stream de respuesta
        return next.handle(requestToHandle).pipe(
            catchError((error: HttpErrorResponse) => {
                // Verificar si el error es 401 (No autorizado) o 403 (Prohibido)
                if (error.status === 401 || error.status === 403) {
                    console.error('Token expirado o no autorizado. Cerrando sesión...');
                    // 🚨 Llama a tu método de logout aquí
                    this.authService.logout();
                }

                // Re-lanza el error para que sea manejado por el componente o servicio que hizo la llamada
                return throwError(() => error);
            })
        );
    }
}
