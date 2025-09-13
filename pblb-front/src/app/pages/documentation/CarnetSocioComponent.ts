import {CommonModule} from '@angular/common';
import {Component, inject, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {CardModule} from 'primeng/card';
import {TableModule} from 'primeng/table';
import {BadgeModule} from 'primeng/badge';
import {ButtonModule} from 'primeng/button';
import {Tooltip} from "primeng/tooltip";
import {DialogModule} from "primeng/dialog";
import {InputTextModule} from "primeng/inputtext";
import {ToastModule} from "primeng/toast";
import {environment} from "../../../enviroments/environment";
import {Socio} from "@/interfaces/socio.interface";
import {Cuota} from "@/interfaces/cuota.interface";
import {ApiResponse} from "@/interfaces/api-response.interface";
import {AppLogo} from "@/layout/component/app.logo";
@Component({
    selector: 'app-carnet-socio',
    standalone: true,
    imports: [
        CommonModule,
        CardModule,
        TableModule,
        BadgeModule,
        ButtonModule,
        Tooltip,
        AppLogo,
        DialogModule,
        InputTextModule,
        FormsModule,
        ToastModule
    ],
    providers: [MessageService],
    templateUrl: 'CarnetSocioComponent.html',
    styleUrl: 'CarnetSocioComponent.scss'
})
export class CarnetSocioComponent implements OnInit {
    socios: Array<Socio> = [];
    cuotas: Cuota[] = [];

    displayNuevoSocioDialog: boolean = false;
    nuevoSocio: Partial<Socio> = {};

    private http = inject(HttpClient);
    private messageService = inject(MessageService);

    ngOnInit(): void {this.cargarSocios();}

    cargarSocios() {
        this.http.get<ApiResponse<Array<Socio>>>(`${environment.apiUrl}/api/socios/me`)
            .subscribe(response => {
                if (response.success && response.data) {
                    this.socios = response.data;
                }
            });
    }

    abrirDialogoNuevoSocio() {
        this.nuevoSocio = {};
        this.displayNuevoSocioDialog = true;
    }

    cerrarDialogoNuevoSocio() {
        this.displayNuevoSocioDialog = false;
    }

    guardarNuevoSocio() {
        // Reutilizamos el endpoint de registro. El backend asociará este nuevo socio
        // al usuario existente porque el email ya está registrado.
        const currentUserEmail = this.socios[0]?.email;
        if (!currentUserEmail) {
            this.messageService.add({severity: 'error', summary: 'Error', detail: 'No se pudo obtener el email del usuario actual.'});
            return;
        }

        const payload = { nombre: this.nuevoSocio.nombre, email: currentUserEmail, password: 'temporaryPassword' }; // La contraseña no se usará

        this.http.post<ApiResponse<Socio>>(`${environment.apiUrl}/api/auth/register`, payload)
            .subscribe({
                next: (response) => {
                    if (response.success) {
                        this.cargarSocios(); // Recargamos la lista de socios
                        this.messageService.add({severity: 'success', summary: 'Éxito', detail: response.message});
                        this.cerrarDialogoNuevoSocio();
                    } else {
                        this.messageService.add({severity: 'error', summary: 'Error', detail: response.message || 'No se pudo crear el socio.'});
                    }
                },
                error: (err) => {
                    const errorMessage = err.error?.message || 'Ocurrió un error en el servidor.';
                    this.messageService.add({severity: 'error', summary: 'Error', detail: errorMessage});
                }
            });
    }
}
