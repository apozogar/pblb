import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DialogModule} from 'primeng/dialog';
import {ButtonModule} from 'primeng/button';
import {FileUploadModule} from 'primeng/fileupload';
import {ToastModule} from 'primeng/toast';
import {MessageService} from 'primeng/api';
import {SocioService} from '@/services/SocioService';

@Component({
    selector: 'app-gestion-cobros',
    standalone: true,
    imports: [CommonModule, DialogModule, ButtonModule, FileUploadModule, ToastModule],
    templateUrl: './gestion-cobros.component.html',
    providers: [MessageService] // Proveedor local para no interferir con otros toasts
})
export class GestionCobrosComponent {
    @Input() visible: boolean = false;
    @Output() visibleChange = new EventEmitter<boolean>();

    loadingGenerar: boolean = false;
    loadingSubir: boolean = false;
    loadingConfirmar: boolean = false;

    constructor(
        private socioService: SocioService,
        private messageService: MessageService
    ) {
    }

    generarYDescargarRemesa() {
        this.loadingGenerar = true;
        this.socioService.generarRemesaMensual().subscribe({
            next: (response) => {
                if (response.data) {
                    this.descargarFicheroXML(response.data, `remesa-sepa-${new Date().toISOString().slice(0, 10)}.xml`);
                    this.messageService.add({severity: 'success', summary: 'Éxito', detail: response.message});
                } else {
                    this.messageService.add({severity: 'warn', summary: 'Aviso', detail: 'No se generaron nuevas cuotas para la remesa.'});
                }
                this.loadingGenerar = false;
            },
            error: (err) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: 'No se pudo generar la remesa.'});
                this.loadingGenerar = false;
            }
        });
    }

    subirFicheroRetorno(event: any) {
        const file: File = event.files[0];
        if (!file) return;

        this.loadingSubir = true;
        this.socioService.procesarRetorno(file).subscribe({
            next: (response) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: response.message});
                this.loadingSubir = false;
                this.cerrarDialogo(); // Cerramos al finalizar
            },
            error: (err) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error al procesar el fichero de retorno.'});
                this.loadingSubir = false;
            }
        });
    }

    confirmarPagosPendientes() {
        this.loadingConfirmar = true;
        this.socioService.confirmarPagos().subscribe({
            next: (response) => {
                this.messageService.add({severity: 'success', summary: 'Éxito', detail: response.message});
                this.loadingConfirmar = false;
                this.cerrarDialogo();
            },
            error: (err) => {
                this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error al confirmar los pagos.'});
                this.loadingConfirmar = false;
            }
        });
    }

    private descargarFicheroXML(xml: string, nombreFichero: string) {
        const blob = new Blob([xml], {type: 'application/xml'});
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreFichero;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    }

    cerrarDialogo() {
        this.visible = false;
        this.visibleChange.emit(this.visible);
    }
}
