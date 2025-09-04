import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from "../../../enviroments/environment";
import {Ingreso} from "@/interfaces/contabilidad.interface";

@Injectable({
    providedIn: 'root'
})
export class IngresoService {
    private apiUrl = environment.apiUrl+ '/api/ingresos';

    constructor(private http: HttpClient) {}

    getIngresos(): Observable<Ingreso[]> {
        return this.http.get<Ingreso[]>(this.apiUrl);
    }

    createIngreso(ingreso: Ingreso): Observable<Ingreso> {
        return this.http.post<Ingreso>(this.apiUrl, ingreso);
    }

    updateIngreso(ingreso: Ingreso): Observable<Ingreso> {
        return this.http.put<Ingreso>(`${this.apiUrl}/${ingreso.uid}`, ingreso);
    }

    deleteIngreso(uid: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${uid}`);
    }
}
