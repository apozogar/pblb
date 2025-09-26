export interface EventoInscripcionDTO {
  uid: string;
  nombreEvento: string;
  fechaEvento: Date;
  ubicacion?: string;
  isCurrentUserInscrito: boolean;
}