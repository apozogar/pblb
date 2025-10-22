import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {ApiResponse} from '@/interfaces/api-response.interface';
import {Pena} from "@/interfaces/socio.interface";

@Injectable({
    providedIn: 'root'
})
export class PenaService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/api/pena`;

    get(id: string): Observable<ApiResponse<Pena>> {
        return this.http.get<ApiResponse<Pena>>(`${this.apiUrl}/${id}`);
    }
}
