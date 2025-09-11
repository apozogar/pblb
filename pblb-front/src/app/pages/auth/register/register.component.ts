import {Component} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {RegisterRequest} from '../../../models/register-request.model';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ButtonModule} from 'primeng/button';
import {CheckboxModule} from 'primeng/checkbox';
import {InputTextModule} from 'primeng/inputtext';
import {PasswordModule} from 'primeng/password';
import {MessageModule} from 'primeng/message';
import {AuthService} from "@/pages/auth/auth.service";
import {AppFloatingConfigurator} from "@/layout/component/app.floatingconfigurator";

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [
        CommonModule,
        RouterLink,
        FormsModule,
        ButtonModule,
        CheckboxModule,
        InputTextModule,
        PasswordModule,
        MessageModule,
        AppFloatingConfigurator
    ],
    templateUrl: './register.component.html',
    styles: ``
})
export class RegisterComponent {

    registerData: RegisterRequest = {
        nombre: '',
        email: '',
        password: ''
    };
    confirmPassword = '';
    error: string | null = null;

    constructor(private authService: AuthService, private router: Router) {
    }

    register(): void {
        this.error = null;

        if (!this.registerData.nombre || !this.registerData.email || !this.registerData.password) {
            this.error = 'Todos los campos son obligatorios.';
            return;
        }

        if (this.registerData.password !== this.confirmPassword) {
            this.error = 'Las contraseñas no coinciden.';
            return;
        }

        this.authService.register(this.registerData).subscribe({
            next: () => {
                // Registro exitoso, redirigir al login o a una página de "verifique su email"
                this.router.navigate(['/auth/login'], {queryParams: {registered: 'true'}});
            },
            error: (err) => {
                // Manejo de errores del backend
                if (err.status === 409) { // Conflict
                    this.error = 'El email ya está registrado.';
                } else {
                    this.error = 'Ocurrió un error durante el registro. Por favor, inténtalo de nuevo.';
                }
                console.error(err);
            }
        });
    }
}
