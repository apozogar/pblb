import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventoInscripcionDTO } from '@/interfaces/evento-inscripcion.dto';
import { Evento } from '@/interfaces/evento.interface';
import { environment } from '../../environments/environment';
import { ApiResponse } from '@/interfaces/api-response.interface';

@Injectable({
  providedIn: 'root'
})
export class EventoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/eventos`;

  /**
   * Obtiene la lista de eventos simplificada para la vista de inscripción de usuarios.
   */
  getEventosParaInscripcion(): Observable<ApiResponse<EventoInscripcionDTO[]>> {
    return this.http.get<ApiResponse<EventoInscripcionDTO[]>>(this.apiUrl);
  }

  /**
   * Obtiene la lista completa de eventos para el panel de administración.
   */
  getEventosParaGestion(): Observable<ApiResponse<Evento[]>> {
    return this.http.get<ApiResponse<Evento[]>>(`${this.apiUrl}/gestion`);
  }

  guardarEvento(evento: Partial<Evento>): Observable<ApiResponse<Evento>> {
    if (evento.uid) {
      return this.http.put<ApiResponse<Evento>>(`${this.apiUrl}/${evento.uid}`, evento);
    } else {
      return this.http.post<ApiResponse<Evento>>(this.apiUrl, evento);
    }
  }

  eliminarEvento(uid: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${uid}`);
  }

  inscribir(eventoId: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/${eventoId}/inscribir`, {});
  }

  anularInscripcion(eventoId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${eventoId}/anular`);
  }
}
