import {Socio} from "@/interfaces/socio.interface";

export interface Ingreso {
    uid?: string;
    concepto?: string;
    monto?: number;
    fechaIngreso?: string; // Se usará un string para las fechas que se enviarán a la API
    tipoIngreso?: string;
    observaciones?: string;
}

export interface Gasto {
    uid?: string;
    concepto?: string;
    monto?: number;
    fechaGasto?: string;
    tipoGasto?: string;
    observaciones?: string;
    rutaFactura?: string; // Ruta al archivo de la factura, opcional
    socio?: Socio; // Objeto del socio que realizó el gasto
}
