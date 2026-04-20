import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiResponse } from '../../../shared/api/api-response.model';
import { Facture } from './facture.models';

@Injectable({ providedIn: 'root' })
export class FactureApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/factures`;

  getAll(): Observable<Facture[]> {
    return this.http.get<ApiResponse<Facture[]>>(this.baseUrl).pipe(map((response) => response.data));
  }

  getById(id: string): Observable<Facture> {
    return this.http.get<ApiResponse<Facture>>(`${this.baseUrl}/${id}`).pipe(map((response) => response.data));
  }

  getByConsultationId(consultationId: string): Observable<Facture> {
    return this.http
      .get<ApiResponse<Facture>>(`${this.baseUrl}/consultation/${consultationId}`)
      .pipe(map((response) => response.data));
  }

  generateByConsultationId(consultationId: string): Observable<Facture> {
    return this.http
      .post<ApiResponse<Facture>>(`${this.baseUrl}/consultation/${consultationId}`, {})
      .pipe(map((response) => response.data));
  }

  markPaid(id: string): Observable<Facture> {
    return this.http
      .patch<ApiResponse<Facture>>(`${this.baseUrl}/${id}/payer`, {})
      .pipe(map((response) => response.data));
  }
}
