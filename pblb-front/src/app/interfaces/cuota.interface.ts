// src/app/interfaces/cuota.interface.ts
export interface Cuota {
  uid?: string;
  socioUid: string;
  monto: number;
  fechaVencimiento: string;
  fechaPago?: string;
  estado: 'PAGADA' | 'PENDIENTE' | 'VENCIDA';
  periodo: 'ANUAL' | 'MENSUAL' | 'PRIMER_TRIMESTRE' | 'SEGUNDO_TRIMESTRE' | 'TERCER_TRIMESTRE' | 'CUARTO_TRIMESTRE';
}
