import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiResponse } from '../../../shared/api/api-response.model';
import { RendezVous, RendezVousUpsertRequest } from './rendezvous.models';

@Injectable({ providedIn: 'root' })
export class RendezVousApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/rendezvous`;

  getAll(): Observable<RendezVous[]> {
    return this.http.get<ApiResponse<RendezVous[]>>(this.baseUrl).pipe(map((response) => response.data));
  }

  getById(id: string): Observable<RendezVous> {
    return this.http.get<ApiResponse<RendezVous>>(`${this.baseUrl}/${id}`).pipe(map((response) => response.data));
  }

  searchByPatient(patientId: string): Observable<RendezVous[]> {
    return this.http
      .get<ApiResponse<RendezVous[]>>(`${this.baseUrl}/search`, { params: { patientId } })
      .pipe(map((response) => response.data));
  }

  create(payload: RendezVousUpsertRequest): Observable<RendezVous> {
    return this.http
      .post<ApiResponse<RendezVous>>(this.baseUrl, payload)
      .pipe(map((response) => response.data));
  }

  update(id: string, payload: RendezVousUpsertRequest): Observable<RendezVous> {
    return this.http
      .put<ApiResponse<RendezVous>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map((response) => response.data));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }
}
