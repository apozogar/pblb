import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Gasto} from "@/interfaces/contabilidad.interface";

@Injectable({
    providedIn: 'root'
})
export class GastoService {
    private apiUrl = 'http://localhost:8080/api/gastos';

    constructor(private http: HttpClient) {}

    getGastos(): Observable<Gasto[]> {
        return this.http.get<Gasto[]>(this.apiUrl);
    }

    createGasto(gasto: Gasto): Observable<Gasto> {
        return this.http.post<Gasto>(this.apiUrl, gasto);
    }

    updateGasto(gasto: Gasto): Observable<Gasto> {
        return this.http.put<Gasto>(`${this.apiUrl}/${gasto.uid}`, gasto);
    }

    deleteGasto(uid: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${uid}`);
    }
}
