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
import {AppLogo} from "@/layout/component/app.logo";


@Component({
    selector: 'app-carnet-socio',
    standalone: true,
    imports: [CommonModule, CardModule, TableModule, BadgeModule, ButtonModule, Tooltip, AppLogo,],
    templateUrl: 'CarnetSocioComponent.html',
    styleUrl: 'CarnetSocioComponent.scss'
})
export class CarnetSocioComponent implements OnInit {
    socios: Array<Socio>  = [];
    cuotas: Cuota[] = [];

    private http = inject(HttpClient);

    ngOnInit(): void {
        this.http.get<ApiResponse<Array<Socio>>>(`${environment.apiUrl}/api/socios/me`)
        .subscribe(response => {
            if (response.success && response.data) {
                this.socios = response.data;
            }
        });
    }

    modificarDatos() {
        // En una aplicación real, aquí se abriría un formulario o modal de edición
        console.log('Botón de "Modificar Datos" clicado.');
    }
}
