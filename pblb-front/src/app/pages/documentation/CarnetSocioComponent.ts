import {CommonModule} from '@angular/common';
import {Component, inject, OnInit} from '@angular/core';
import {CardModule} from 'primeng/card';
import {TableModule} from 'primeng/table';
import {BadgeModule} from 'primeng/badge';
import {ButtonModule} from 'primeng/button';
import {Tooltip} from "primeng/tooltip";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../enviroments/environment";
import {Socio} from "@/interfaces/socio.interface";
import {Cuota} from "@/interfaces/cuota.interface";
import {ApiResponse} from "@/interfaces/api-response.interface";


@Component({
    selector: 'app-carnet-socio',
    standalone: true,
    imports: [CommonModule, CardModule, TableModule, BadgeModule, ButtonModule, Tooltip,],
    templateUrl: 'CarnetSocioComponent.html',
    styleUrl: 'CarnetSocioComponent.scss'
})
export class CarnetSocioComponent implements OnInit {
    socio: Socio | null = null;
    cuotas: Cuota[] = [];

    private http = inject(HttpClient);

    ngOnInit(): void {
        this.http.get<ApiResponse<Socio>>(`${environment.apiUrl}/api/socios/me`)
        .subscribe(response => {
            if (response.success && response.data) {
                this.socio = response.data;
                // Asumimos que las cuotas vienen dentro del objeto socio.
                // Si no es así, habría que hacer otra llamada a /api/socios/{id}/cuotas
                this.cuotas = response.data.cuotas || [];
            }
        });
    }

    modificarDatos() {
        // En una aplicación real, aquí se abriría un formulario o modal de edición
        console.log('Botón de "Modificar Datos" clicado.');
    }
}
