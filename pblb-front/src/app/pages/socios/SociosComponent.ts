import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MessageService, ConfirmationService} from 'primeng/api';
import {TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {ToastModule} from 'primeng/toast';
import {ToolbarModule} from 'primeng/toolbar';
import {DialogModule} from 'primeng/dialog';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {TagModule} from 'primeng/tag';
import {SocioService} from '../service/SocioService';
import {Ripple} from 'primeng/ripple';
import {CheckboxModule} from 'primeng/checkbox';
import {DatePickerModule} from 'primeng/datepicker';
import {Textarea} from 'primeng/textarea';
import {EstadisticasSocio} from "@/interfaces/socio.interface";
import {IconField} from "primeng/iconfield";
import {InputIcon} from "primeng/inputicon";

@Component({
    selector: 'app-socios',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        TableModule,
        ButtonModule,
        InputTextModule,
        ToastModule,
        ToolbarModule,
        DialogModule,
        ConfirmDialogModule,
        TagModule,
        Ripple,
        CheckboxModule,
        DatePickerModule,
        Textarea, IconField, InputIcon

    ],
    templateUrl: './SociosComponent.html'
})
export class SociosComponent implements OnInit {
    socios: any[] = [];
    socio: any = {};
    socioDialog: boolean = false;
    loading: boolean = false;
    submitted: boolean = false;

    cuotasDialog: boolean = false;
    cuotasSocio: any[] = [];

    estadistica?: EstadisticasSocio;

    constructor(
        private readonly socioService: SocioService,
        private readonly messageService: MessageService,
        private readonly confirmationService: ConfirmationService
    ) {
    }

    ngOnInit(): void {
        this.cargarSocios();
    }

    cargarSocios(): void {
        this.loading = true;
        this.obtenerEstatidicas();
        this.socioService.getSocios().subscribe({
            next: (response: any) => {
                this.socios = response.data;
                this.loading = false;
            },
            error: () => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'Error al cargar los socios'
                });
                this.loading = false;
            }
        });
    }

    abrirNuevo(): void {
        this.socio = {};
        this.socioDialog = true;
    }

    editarSocio(socio: any): void {
        this.socio = {...socio};
        this.socioDialog = true;
    }

    guardarSocio(): void {
        this.submitted = true;

        if (this.socio.uid) {
            this.socioService.actualizarSocio(this.socio.uid, this.socio).subscribe({
                next: () => {
                    this.messageService.add({
                        severity: 'success',
                        summary: 'Éxito',
                        detail: 'Socio actualizado'
                    });
                    this.cargarSocios();
                    this.socioDialog = false;
                }
            });
        } else {
            this.socioService.crearSocio(this.socio).subscribe({
                next: () => {
                    this.messageService.add({
                        severity: 'success',
                        summary: 'Éxito',
                        detail: 'Socio creado'
                    });
                    this.cargarSocios();
                    this.socioDialog = false;
                }
            });
        }
    }

    eliminarSocio(socio: any): void {
        this.confirmationService.confirm({
            message: '¿Está seguro que desea eliminar este socio?',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                this.socioService.eliminarSocio(socio.uid).subscribe({
                    next: () => {
                        this.cargarSocios();
                        this.messageService.add({
                            severity: 'success',
                            summary: 'Éxito',
                            detail: 'Socio eliminado'
                        });
                    }
                });
            }
        });
    }

    cobrarCuota(): void {
        this.socioService.generarSepa().subscribe((data) => {
            // Suponiendo que `data` contiene el contenido del archivo que deseas descargar
            const blob = new Blob([data], {type: 'application/octet-stream'});
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = 'sepa.xml'; // Cambia el nombre del archivo según sea necesario
            document.body.appendChild(a);
            a.click();

            // Limpieza
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        });
    }

    mostrarCuotas(socio: any): void {
        this.socioService.getCuotasSocio(socio.uid).subscribe({
            next: (response) => {
                this.cuotasSocio = response.data;
                this.cuotasDialog = true;
            },
            error: () => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'No se pudieron cargar las cuotas del socio.'
                });
            }
        });
    }


    obtenerEstatidicas(): void {
        this.socioService.obtenerEstadisticas().subscribe((data) => {
            this.estadistica = data.data;
        });
    }

    onFileSelect(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            const file = input.files[0];
            this.subirFichero(file);
        }
    }

    private subirFichero(file: File): void {
        this.loading = true; // Mostramos el spinner de la tabla
        this.socioService.importarSocios(file).subscribe({
            next: (response) => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: response.message
                });
                this.cargarSocios(); // Recargamos la lista de socios para ver los nuevos
            },
            error: (err) => {
                this.loading = false;
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: 'Hubo un error al importar el fichero.'
                });
            }
        });
    }
}
