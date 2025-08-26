import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { ToolbarModule } from 'primeng/toolbar';
import { DialogModule } from 'primeng/dialog';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TagModule } from 'primeng/tag';
import {SocioService} from '../service/SocioService';
import {Ripple} from 'primeng/ripple';
import {CheckboxModule} from 'primeng/checkbox';
import {DatePickerModule} from 'primeng/datepicker';
import {Textarea} from 'primeng/textarea';

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
    Textarea

  ],
  templateUrl: './SociosComponent.html'
})
export class SociosComponent implements OnInit {
  socios: any[] = [];
  socio: any = {};
  socioDialog: boolean = false;
  loading: boolean = false;
  submitted: boolean = false;


  constructor(
    private readonly socioService: SocioService,
    private readonly messageService: MessageService,
    private readonly confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.cargarSocios();
  }

  cargarSocios(): void {
    this.loading = true;
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
    this.socio = { ...socio };
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
}
