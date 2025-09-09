import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { RippleModule } from 'primeng/ripple';
import { AppFloatingConfigurator } from '../../layout/component/app.floatingconfigurator';
import { AuthService } from './auth.service';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ButtonModule, CheckboxModule, InputTextModule, PasswordModule, FormsModule, RouterModule, RippleModule, AppFloatingConfigurator],
    template: `
        <app-floating-configurator/>
        <div
            class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-screen overflow-hidden">
            <div class="flex flex-col items-center justify-center">
                <div
                    style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)">
                    <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20"
                         style="border-radius: 53px">
                        <div class="text-center mb-8">
                            <div
                                class="text-surface-900 dark:text-surface-0 text-3xl font-medium mb-4">
                                ¡Bienvenido!
                            </div>
                            <span
                                class="text-muted-color font-medium">Inicia sesión para continuar</span>
                        </div>

                        <div>
                            <label for="email1"
                                   class="block text-surface-900 dark:text-surface-0 text-xl font-medium mb-2">Email</label>
                            <input pInputText id="email1" type="text"
                                   placeholder="Dirección de email" class="w-full md:w-120 mb-8"
                                   [(ngModel)]="email"/>

                            <label for="password"
                                   class="block text-surface-900 dark:text-surface-0 font-medium text-xl mb-2">Contraseña</label>
                            <p-password id="password" [(ngModel)]="password"
                                        placeholder="Contraseña" [toggleMask]="true"
                                        styleClass="mb-4" [fluid]="true"
                                        [feedback]="false"></p-password>

                            <div *ngIf="error" class="p-error text-center mb-4">{{ error }}</div>

                            <div class="flex items-center justify-between mt-2 mb-8 gap-8">
                                <div class="flex items-center">
                                    <p-checkbox [(ngModel)]="checked" id="rememberme" binary
                                                class="mr-2"></p-checkbox>
                                    <label for="rememberme">Recuérdame</label>
                                </div>
                                <a [routerLink]="['/auth/forgot-password']"
                                   class="font-medium no-underline ml-2 text-right cursor-pointer text-primary">¿Olvidaste
                                    tu contraseña?</a>
                            </div>
                            <p-button label="Iniciar Sesión" styleClass="w-full"
                                      (click)="login()"></p-button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `
})
export class Login {
    email: string = '';
    password: string = '';
    checked: boolean = false;
    error: string | null = null;

    private authService = inject(AuthService);
    private router = inject(Router);

    login(): void {
        this.error = null;
        if (this.email && this.password) {
            this.authService.login({ email: this.email, password: this.password })
                .subscribe({
                    next: () => {
                        this.router.navigate(['/']);
                    },
                    error: (err) => {
                        console.error(err);
                        this.error = 'Email o contraseña incorrectos. Por favor, inténtalo de nuevo.';
                    }
                });
        } else {
            this.error = 'El email y la contraseña son obligatorios.';
        }
    }
}
