import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { TableModule } from 'primeng/table';
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
    DatePickerModule
  ],
  template: `
    <div class="card">
      <p-toast></p-toast>
      <p-toolbar styleClass="mb-4">
        <ng-template pTemplate="left">
          <button pButton pRipple label="Nuevo Evento" icon="pi pi-plus"
                  class="p-button-success mr-2" (click)="abrirNuevo()"></button>
        </ng-template>
      </p-toolbar>

      <p-table [value]="eventos" [rows]="10" [paginator]="true"
               [rowHover]="true" dataKey="id" [loading]="loading"
               styleClass="p-datatable-gridlines">
        <ng-template pTemplate="header">
          <tr>
            <th>Nombre</th>
            <th>Fecha</th>
            <th>Ubicación</th>
            <th>Coste Estimado</th>
            <th>Participantes</th>
            <th>Acciones</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-evento>
          <tr>
            <td>{{ evento.nombreEvento }}</td>
            <td>{{ evento.fechaEvento | date:'dd/MM/yyyy' }}</td>
            <td>{{ evento.ubicacion }}</td>
            <td>{{ evento.costeTotalEstimado | currency:'EUR' }}</td>
            <td>{{ evento.participantes }}</td>
            <td>
              <button pButton pRipple icon="pi pi-pencil"
                      class="p-button-rounded p-button-success mr-2"
                      (click)="editarEvento(evento)"></button>
              <button pButton pRipple icon="pi pi-trash"
                      class="p-button-rounded p-button-danger"
                      (click)="eliminarEvento(evento)"></button>
            </td>
          </tr>
        </ng-template>
      </p-table>
    </div>

    <p-dialog [(visible)]="eventoDialog" [style]="{width: '450px'}"
              header="Detalles del Evento" [modal]="true" class="p-fluid">
      <ng-template pTemplate="content">
        <div class="field">
          <label for="nombre">Nombre</label>
          <input type="text" pInputText id="nombre"
                 [(ngModel)]="evento.nombreEvento" required/>
        </div>
        <div class="field">
          <label for="fecha">Fecha</label>
          <p-datepicker
            id="fecha"
            [(ngModel)]="evento.fechaEvento"
            dateFormat="dd/mm/yy"
            [showIcon]="true">
          </p-datepicker>
        </div>
        <div class="field">
          <label for="ubicacion">Ubicación</label>
          <input type="text" pInputText id="ubicacion"
                 [(ngModel)]="evento.ubicacion"/>
        </div>
        <div class="field">
          <label for="coste">Coste Estimado</label>
          <p-inputNumber id="coste" [(ngModel)]="evento.costeTotalEstimado"
                         mode="currency" currency="EUR"></p-inputNumber>
        </div>

        <div class="field">
          <label for="descripcion">Descripción</label>
          <textarea pTextarea
                    id="descripcion"
                    [(ngModel)]="evento.descripcion"
                    [rows]="3"
                    [autoResize]="true"></textarea>
        </div>
      </ng-template>
      <ng-template pTemplate="footer">
        <button pButton pRipple label="Cancelar" icon="pi pi-times"
                class="p-button-text" (click)="eventoDialog = false"></button>
        <button pButton pRipple label="Guardar" icon="pi pi-check"
                class="p-button-text" (click)="guardarEvento()"></button>
      </ng-template>
    </p-dialog>
  `
})

export class EventosComponent implements OnInit {
  eventos: any[] = [];
  evento: any = {};
  eventoDialog: boolean = false;
  loading: boolean = false;

  constructor(
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit() {
    // Aquí cargarías los eventos desde tu servicio
    this.cargarEventos();
  }

  cargarEventos() {
    this.loading = true;
    // Implementar la carga de eventos
    this.loading = false;
  }

  abrirNuevo() {
    this.evento = {};
    this.eventoDialog = true;
  }

  editarEvento(evento: any) {
    this.evento = { ...evento };
    this.eventoDialog = true;
  }

  eliminarEvento(evento: any) {
    this.confirmationService.confirm({
      message: '¿Está seguro que desea eliminar este evento?',
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        // Implementar eliminación
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Evento eliminado'
        });
      }
    });
  }

  guardarEvento() {
    // Implementar guardado
    this.eventoDialog = false;
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: 'Evento guardado'
    });
  }
}
