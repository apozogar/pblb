export interface Socio {
  uid?: string;
  numeroSocio: string;
  nombre: string;
  apellidos: string;
  fechaNacimiento: string;
  dni: string;
  direccion?: string;
  telefono?: string;
  email: string;
  fechaAlta: string;
  activo: boolean;
  observaciones?: string;
}
