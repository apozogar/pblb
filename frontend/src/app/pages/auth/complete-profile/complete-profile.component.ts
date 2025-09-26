import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {MessageModule} from 'primeng/message';
import {ProgressSpinnerModule} from 'primeng/progressspinner';
import {DatePicker} from "primeng/datepicker";
import {SocioService} from "@/services/SocioService";
import {Socio} from "@/interfaces/socio.interface";
import {UsuarioService} from "@/services/UsuarioService";

@Component({
    selector: 'app-complete-profile',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        InputTextModule,
        DatePicker,
        MessageModule,
        ProgressSpinnerModule
    ],
    templateUrl: './complete-profile.component.html',
})
export class CompleteProfileComponent implements OnInit {

    socio: Socio | null = null;
    error: string | null = null;

    constructor(
        private socioService: SocioService,
        private usuarioService: UsuarioService,
        private router: Router
    ) {
    }

    ngOnInit(): void {
        this.usuarioService.getMyPrincipalSocio().subscribe({
            next: (response) => {
                this.socio = response.data;
                // Convertir la fecha a objeto Date para el p-calendar si viene como string
                if (this.socio && this.socio.fechaNacimiento) {
                    this.socio.fechaNacimiento = new Date(this.socio.fechaNacimiento);
                }
            },
            error: (err) => {
                this.error = 'No se pudo cargar tu perfil de socio. Por favor, contacta con soporte.';
                console.error(err);
            }
        });
    }

    saveProfile(): void {
        if (!this.socio) return;

        this.usuarioService.updateMySocio(this.socio.uid!, this.socio).subscribe({
            next: () => {
                // Perfil completado, redirigir al dashboard principal
                this.router.navigate(['/']);
            },
            error: (err) => {
                this.error = 'Hubo un error al guardar tu perfil. Inténtalo de nuevo.';
                console.error(err);
            }
        });
    }

    skip(): void {
        // El usuario decide completar el perfil más tarde, lo llevamos al dashboard
        this.router.navigate(['/']);
    }
}
