import {Cuota} from "@/interfaces/cuota.interface";

export interface Pena {
    id: number;
    nombre: string;
    // Añade aquí otros campos de la peña que necesites
}

export interface Socio {
    uid?: string;
    numeroSocio: string;
    nombre: string;
    apellidos: string;
    fechaNacimiento: string | Date;
    dni: string;
    direccion?: string;
    poblacion?: string;
    provincia?: string;
    codigoPostal?: string;
    telefono?: string;
    email: string;
    fechaAlta: string;
    numeroCuenta: string;
    activo: boolean;
    abonadoBetis: boolean;
    accionistaBetis: boolean;
    observaciones?: string;
    cuotas: Cuota[];
}

export interface EstadisticasSocio {
    totalSocios: number;
    nuevosSocios: number;
    totalSociosJovenes: number;
    edadMayoria: number;
    totalSociosJubilados: number;
    edadJubilacion: number;
    totalImpagados: number;
}

export interface CarnetDto {
    penaInfo: Pena;
    socios: Socio[];
}
