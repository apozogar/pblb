import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '@/interfaces/api-response.interface';
import {Socio} from '@/interfaces/socio.interface';
import {environment} from "../../../enviroments/environment";


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
}
