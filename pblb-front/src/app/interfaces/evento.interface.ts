export interface Evento {
    uid?: string;
    nombreEvento: string;
    fechaEvento: Date;
    ubicacion?: string;
    descripcion?: string;
    numeroPlazas?: number;
    costeTotalEstimado?: number;
    costeTotalReal?: number;
    isCurrentUserInscrito?: boolean;
    // participaciones?: any[]; // No es necesario para el formulario de momento
}
