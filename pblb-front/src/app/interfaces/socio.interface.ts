import {Cuota} from "@/interfaces/cuota.interface";

export interface Socio {
    uid?: string;
    numeroSocio: string;
    nombre: string;
    apellidos: string;
    fechaNacimiento: string;
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
}

