// src/app/interfaces/evento.interface.ts
export interface Evento {
  uid?: string;
  nombreEvento: string;
  fechaEvento: string;
  ubicacion: string;
  descripcion?: string;
  costeTotalEstimado?: number;
  costeTotalReal?: number;
  participantes?: number;
}
