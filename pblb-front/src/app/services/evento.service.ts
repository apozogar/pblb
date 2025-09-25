import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento } from '@/interfaces/evento.interface';
import { environment } from '../../enviroments/environment';
import { ApiResponse } from '@/interfaces/api-response.interface';

@Injectable({
  providedIn: 'root'
})
export class EventoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/eventos`;

  getEventos(): Observable<ApiResponse<Evento[]>> {
    return this.http.get<ApiResponse<Evento[]>>(this.apiUrl);
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
}