import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '@/interfaces/api-response.interface';
import {Socio} from '@/interfaces/socio.interface';
import {environment} from "../../enviroments/environment";


@Injectable({
    providedIn: 'root'
})
export class UsuarioService {
    private apiUrl = `${environment.apiUrl}/api`;

    constructor(private http: HttpClient) {
    }

    getMyPrincipalSocio(): Observable<ApiResponse<Socio>> {
        return this.http.get<ApiResponse<Socio>>(`${this.apiUrl}/usuarios/me/socio-principal`);
    }

    updateMySocio(id: string, socio: Socio): Observable<ApiResponse<Socio>> {
        return this.http.put<ApiResponse<Socio>>(`${this.apiUrl}/socios/me/${id}`, socio);
    }
}
