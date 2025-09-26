import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Evento } from '@/interfaces/evento.interface';
import { EventoService } from '@/services/evento.service';
import { MessageService } from 'primeng/api';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';

@Component({
  selector: 'app-inscripcion-eventos',
  standalone: true,
  imports: [CommonModule, CardModule, ButtonModule, ToastModule, ProgressSpinnerModule],
  templateUrl: './inscripcion-eventos.component.html',
  styleUrls: ['./inscripcion-eventos.component.scss'],
  providers: [MessageService]
})
export class InscripcionEventosComponent implements OnInit {
  eventos: Evento[] = [];
  loading = true;

  private eventoService = inject(EventoService);
  private messageService = inject(MessageService);

  ngOnInit(): void {
    this.eventoService.getEventos().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.eventos = response.data;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar los eventos.' });
      }
    });
  }

  inscribir(evento: Evento) {
    if (!evento.uid) return;

    this.eventoService.inscribir(evento.uid).subscribe({
      next: () => {
        evento.isCurrentUserInscrito = true; // Actualización optimista
        this.messageService.add({
          severity: 'success',
          summary: '¡Inscrito!',
          detail: `Te has inscrito correctamente a ${evento.nombreEvento}`
        });
      },
      error: (err) => this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: err.error.message || 'No se pudo realizar la inscripción.'
      })
    });
  }

  anularInscripcion(evento: Evento) {
    if (!evento.uid) return;

    this.eventoService.anularInscripcion(evento.uid).subscribe({
      next: () => {
        evento.isCurrentUserInscrito = false; // Actualización optimista
        this.messageService.add({
          severity: 'info',
          summary: 'Anulado',
          detail: `Has anulado tu inscripción a ${evento.nombreEvento}`
        });
      },
      error: (err) => this.messageService.add({
        severity: 'error', summary: 'Error', detail: err.error.message || 'No se pudo anular la inscripción.'
      })
    });
  }
}