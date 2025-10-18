import {Component, inject, OnInit, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MessageService, ConfirmationService} from 'primeng/api';
import {Table, TableModule} from 'primeng/table'; // Import Table
import {Socio} from '@/interfaces/socio.interface'; // Import Socio interface for participants
import {Evento} from '@/interfaces/evento.interface'; // Import Evento interface
import {ButtonModule} from 'primeng/button';
import {InputTextModule} from 'primeng/inputtext';
import {InputNumberModule} from 'primeng/inputnumber';
import {ToastModule} from 'primeng/toast';
import {ToolbarModule} from 'primeng/toolbar';
import {DialogModule} from 'primeng/dialog';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {CardModule} from 'primeng/card';
import {TextareaModule} from 'primeng/textarea';
import {DatePickerModule} from 'primeng/datepicker';
import {IconFieldModule} from 'primeng/iconfield'; // Import IconFieldModule
import {InputIconModule} from 'primeng/inputicon'; // Import InputIconModule
import {EventoService} from '@/services/evento.service';
import {Tooltip} from "primeng/tooltip";


@Component({
    selector: 'app-eventos',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        TableModule,
        ButtonModule,
        InputTextModule,
        InputNumberModule,
        ToastModule,
        ToolbarModule,
        DialogModule,
        ConfirmDialogModule,
        CardModule,
        TextareaModule,
        DatePickerModule,
        IconFieldModule, // Add IconFieldModule
        InputIconModule,
        Tooltip,
        // Add InputIconModule
    ],
    templateUrl: './EventosComponent.html',
    styleUrls: ['./EventosComponent.scss'],
    providers: [MessageService, ConfirmationService]
})

export class EventosComponent implements OnInit { // Implement OnInit
    eventos: Evento[] = []; // Use Evento interface
    evento: Partial<Evento> = {}; // Use Partial<Evento> for form
    eventoDialog: boolean = false;
    displayParticipantesDialog: boolean = false; // New property for participants dialog
    participantesEventoSeleccionado: Socio[] = []; // New property for selected event's participants
    loading: boolean = false;

    private eventoService = inject(EventoService);
    private messageService = inject(MessageService);
    private confirmationService = inject(ConfirmationService);

    public numEventos = 0;
    public numEventosPendientes = 0;


    @ViewChild('dt') dt: Table | undefined; // Reference to the p-table for global filter

    ngOnInit() {
        // Aquí cargarías los eventos desde tu servicio
        this.cargarEventos();
    }

    cargarEventos() {
        this.loading = true;
        this.eventoService.getEventosParaGestion().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    this.eventos = response.data;
                    this.numEventos = this.eventos.length;
                    this.eventos.forEach((p) => {
                        p.fechaEvento = new Date(p.fechaEvento);
                        if (p.fechaEvento < new Date()) {
                            this.numEventosPendientes += 1;
                        }
                    });
                }
                this.loading = false;
            },
            error: () => {
                this.loading = false;
            }
        });
    }

    abrirNuevo() {
        this.evento = {nombreEvento: ''}; // Initialize with required fields
        this.eventoDialog = true;
    }

    editarEvento(evento: Evento) { // Use Evento interface
        this.evento = {...evento}; // Create a copy to avoid direct modification
        this.eventoDialog = true;
    }

    mostrarParticipantes(evento: Evento) {
        if (evento.participantes) {
            this.participantesEventoSeleccionado = Array.from(evento.participantes); // Convert Set to Array for p-table
        } else {
            this.participantesEventoSeleccionado = [];
        }
        this.displayParticipantesDialog = true;
    }

    eliminarEvento(evento: Evento) {
        this.confirmationService.confirm({
            message: '¿Está seguro que desea eliminar este evento?',
            header: 'Confirmar',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                if (!evento.uid) return;
                this.eventoService.eliminarEvento(evento.uid).subscribe({
                    next: () => {
                        this.messageService.add({
                            severity: 'success',
                            summary: 'Éxito',
                            detail: 'Evento eliminado'
                        });
                        this.cargarEventos(); // Recargar la lista
                    },
                    error: (err) => {
                        this.messageService.add({
                            severity: 'error',
                            summary: 'Error',
                            detail: err.error.message || 'No se pudo eliminar el evento'
                        });
                    }
                });
            }
        });
    }

    guardarEvento() { // No need for 'any' here, as 'this.evento' is already typed
        this.eventoService.guardarEvento(this.evento).subscribe({
            next: () => {
                this.messageService.add({
                    severity: 'success',
                    summary: 'Éxito',
                    detail: 'Evento guardado correctamente'
                });
                this.eventoDialog = false;
                this.cargarEventos(); // Recargar la lista
            },
            error: (err) => {
                this.messageService.add({
                    severity: 'error',
                    summary: 'Error',
                    detail: err.error.message || 'No se pudo guardar el evento'
                });
            }
        });
    }
}
