import {Socio} from "@/interfaces/socio.interface";

export interface Evento {
    uid?: string;
    nombreEvento: string;
    fechaEvento: Date;
    ubicacion?: string;
    descripcion?: string;
    numeroPlazas?: number;
    costeTotalEstimado?: number;
    costeTotalReal?: number;
    participantes?: Set<Socio>; // Add this field
    isCurrentUserInscrito?: boolean;
    // participaciones?: any[]; // No es necesario para el formulario de momento
}
