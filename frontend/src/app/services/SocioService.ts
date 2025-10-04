import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '@/interfaces/api-response.interface';
import {EstadisticasSocio, Socio} from '@/interfaces/socio.interface';
import {environment} from "../../enviroments/environment";
import {Role} from "@/interfaces/role.interface";


@Injectable({
    providedIn: 'root'
})
export class SocioService {
    private apiUrl = `${environment.apiUrl}/api/socios`;

    constructor(private http: HttpClient) {
    }

    getSocios(): Observable<ApiResponse<Socio[]>> {
        return this.http.get<ApiResponse<Socio[]>>(this.apiUrl);
    }

    getSociosActivos(): Observable<ApiResponse<Socio[]>> {
        return this.http.get<ApiResponse<Socio[]>>(`${this.apiUrl}/activos`);
    }

    getSocio(uid: string): Observable<ApiResponse<Socio>> {
        return this.http.get<ApiResponse<Socio>>(`${this.apiUrl}/${uid}`);
    }

    crearSocio(socio: Socio): Observable<ApiResponse<Socio>> {
        return this.http.post<ApiResponse<Socio>>(this.apiUrl, socio);
    }

    actualizarSocio(uid: string, socio: Socio): Observable<ApiResponse<Socio>> {
        return this.http.put<ApiResponse<Socio>>(`${this.apiUrl}/${uid}`, socio);
    }

    eliminarSocio(uid: string): Observable<ApiResponse<void>> {
        return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${uid}`);
    }

    getCuotasSocio(uid: string): Observable<ApiResponse<any[]>> {
        return this.http.get<ApiResponse<any[]>>(`${this.apiUrl}/${uid}/cuotas`);
    }

    // Métodos adicionales para validación
    verificarDniExistente(dni: string): Observable<boolean> {
        return this.http.get<boolean>(`${this.apiUrl}/verificar/dni/${dni}`);
    }

    verificarEmailExistente(email: string): Observable<boolean> {
        return this.http.get<boolean>(`${this.apiUrl}/verificar/email/${email}`);
    }

    verificarNumeroSocioExistente(numeroSocio: string): Observable<boolean> {
        return this.http.get<boolean>(`${this.apiUrl}/verificar/numero/${numeroSocio}`);
    }

    obtenerEstadisticas(): Observable<ApiResponse<EstadisticasSocio>> {
        return this.http.get<ApiResponse<EstadisticasSocio>>(`${this.apiUrl}/estadisticas`);
    }

    generarSepa(): Observable<Blob> { // Debe devolver un Observable<Blob>
        return this.http.get(`${this.apiUrl}/generar-sepa`, {
            responseType: 'blob' // ¡Esta es la clave!
        });
    }

    importarSocios(file: File): Observable<ApiResponse<string>> {
        const formData = new FormData();
        formData.append('file', file, file.name);

        return this.http.post<ApiResponse<string>>(`${this.apiUrl}/importar`, formData);
    }

    generarRemesaMensual(): Observable<ApiResponse<string>> {
        return this.http.post<ApiResponse<string>>(`${environment.apiUrl}/api/cobros/generar-remesa`, {});
    }

    getRoles(): Observable<ApiResponse<Role[]>> {
        return this.http.get<ApiResponse<Role[]>>(`${environment.apiUrl}/api/roles`);
    }

    procesarRetorno(file: File): Observable<ApiResponse<string>> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<ApiResponse<string>>(`${environment.apiUrl}/api/cobros/procesar-retorno`, formData);
    }

    confirmarPagos(): Observable<ApiResponse<string>> {
        return this.http.post<ApiResponse<string>>(`${environment.apiUrl}/api/cobros/confirmar-pagos`, {});
    }

}
