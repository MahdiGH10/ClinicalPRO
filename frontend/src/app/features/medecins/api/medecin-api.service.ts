import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiResponse } from '../../../shared/api/api-response.model';
import { Medecin, MedecinUpsertRequest } from './medecin.models';

@Injectable({ providedIn: 'root' })
export class MedecinApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/medecins`;

  getAll(): Observable<Medecin[]> {
    return this.http.get<ApiResponse<Medecin[]>>(this.baseUrl).pipe(map((response) => response.data));
  }

  getById(id: string): Observable<Medecin> {
    return this.http.get<ApiResponse<Medecin>>(`${this.baseUrl}/${id}`).pipe(map((response) => response.data));
  }

  searchByNom(nom: string): Observable<Medecin[]> {
    return this.http
      .get<ApiResponse<Medecin[]>>(`${this.baseUrl}/search`, { params: { nom } })
      .pipe(map((response) => response.data));
  }

  create(payload: MedecinUpsertRequest): Observable<Medecin> {
    return this.http
      .post<ApiResponse<Medecin>>(this.baseUrl, payload)
      .pipe(map((response) => response.data));
  }

  update(id: string, payload: MedecinUpsertRequest): Observable<Medecin> {
    return this.http
      .put<ApiResponse<Medecin>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map((response) => response.data));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }
}
