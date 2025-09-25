import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Table, TableModule } from 'primeng/table'; // Import Table
import { Evento } from '@/interfaces/evento.interface'; // Import Evento interface
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { CardModule } from 'primeng/card';
import { TextareaModule } from 'primeng/textarea';
import { DatePickerModule } from 'primeng/datepicker';
import { IconFieldModule } from 'primeng/iconfield'; // Import IconFieldModule
import { InputIconModule } from 'primeng/inputicon'; // Import InputIconModule
import { EventoService } from '@/services/evento.service';


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
    InputIconModule // Add InputIconModule
  ],
  templateUrl: './EventosComponent.html',
  styleUrls: ['./EventosComponent.scss'],
  providers: [MessageService, ConfirmationService]
})

export class EventosComponent implements OnInit { // Implement OnInit
  eventos: Evento[] = []; // Use Evento interface
  evento: Partial<Evento> = {}; // Use Partial<Evento> for form
  eventoDialog: boolean = false;
  loading: boolean = false;

  private eventoService = inject(EventoService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);


  @ViewChild('dt') dt: Table | undefined; // Reference to the p-table for global filter

  ngOnInit() {
    // Aquí cargarías los eventos desde tu servicio
    this.cargarEventos();
  }

  cargarEventos() {
    this.loading = true;
    this.eventoService.getEventos().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.eventos = response.data;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  abrirNuevo() {
    this.evento = { nombreEvento: '' }; // Initialize with required fields
    this.eventoDialog = true;
  }

  editarEvento(evento: Evento) { // Use Evento interface
    this.evento = { ...evento }; // Create a copy to avoid direct modification
    this.eventoDialog = true;
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
