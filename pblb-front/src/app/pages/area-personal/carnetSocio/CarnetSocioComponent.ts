import {CommonModule} from '@angular/common';
import {Component, inject, OnInit, ViewChild} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
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
import {CarnetDto, Pena, Socio} from "@/interfaces/socio.interface";
import {ApiResponse} from "@/interfaces/api-response.interface";
import {AppLogo} from "@/layout/component/app.logo";
import {Carousel} from "primeng/carousel";
import {CuotasSocioTableComponent} from "@/components/cuotas-socio-table/cuotas-socio-table.component";
import {SocioFormComponent} from "@/components/socio-form/socio-form.component";
import {environment} from "../../../../enviroments/environment";

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
        ToastModule,
        Carousel,
        CuotasSocioTableComponent,
        SocioFormComponent
    ],
    providers: [MessageService],
    templateUrl: 'CarnetSocioComponent.html',
    styleUrl: 'CarnetSocioComponent.scss'
})
export class CarnetSocioComponent implements OnInit {
    penaInfo: Pena | null = null;
    socios: Socio[] = []; // Cada socio ya tendrá sus cuotas

    displayNuevoSocioDialog: boolean = false;
    nuevoSocio: Partial<Socio> = {};

    @ViewChild(SocioFormComponent) socioFormComponent!: SocioFormComponent;

    private http = inject(HttpClient);
    private messageService = inject(MessageService);

    ngOnInit(): void {
        this.cargarDatosCarnet();
    }

    cargarDatosCarnet() {
        this.http.get<ApiResponse<CarnetDto>>(`${environment.apiUrl}/api/socios/me`)
            .subscribe(response => {
                if (response.success && response.data) {
                    this.penaInfo = response.data.penaInfo;
                    this.socios = response.data.socios;
                }
            });
    }

    abrirDialogoNuevoSocio() {
        // Inicializamos el nuevo socio. El email se tomará del socio principal en el backend.
        // La cuenta bancaria se heredará si se deja en blanco.
        this.nuevoSocio = { nombre: '' };
        this.displayNuevoSocioDialog = true;
    }

    cerrarDialogoNuevoSocio() {
        this.displayNuevoSocioDialog = false;
    }

    guardarNuevoSocio(socio: Partial<Socio>) {
        // El endpoint /api/socios/me/asociado está pensado para esto:
        // crea un nuevo socio y lo asocia al usuario autenticado.
        // No es necesario enviar el email del usuario, el backend lo obtiene del token de autenticación.
        const payload = { ...socio };

        this.http.post<ApiResponse<Socio>>(`${environment.apiUrl}/api/socios/me/asociado`, payload)
            .subscribe({
                next: (response) => {
                    if (response.success) {
                        this.cargarDatosCarnet(); // Recargamos toda la información
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
