import {Component, Input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TableModule} from 'primeng/table';
import {BadgeModule} from 'primeng/badge';
import {CardModule} from "primeng/card";
import {Cuota} from "@/interfaces/cuota.interface";

@Component({
  selector: 'app-cuotas-socio-table',
  standalone: true,
  imports: [CommonModule, TableModule, BadgeModule, CardModule],
  templateUrl: './cuotas-socio-table.component.html',
})
export class CuotasSocioTableComponent {
  @Input() cuotas: Cuota[] = [];

  getSeverity(estado: string): 'success' | 'danger' | 'warn' {
    switch (estado) {
      case 'PAGADA':
        return 'success';
      case 'PENDIENTE':
        return 'warn';
      default:
        return 'danger';
    }
  }
}
