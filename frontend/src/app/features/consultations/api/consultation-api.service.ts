import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiResponse } from '../../../shared/api/api-response.model';
import { Consultation, ConsultationCreateRequest } from './consultation.models';

@Injectable({ providedIn: 'root' })
export class ConsultationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/consultations`;

  getAll(): Observable<Consultation[]> {
    return this.http.get<ApiResponse<Consultation[]>>(this.baseUrl).pipe(map((response) => response.data));
  }

  getById(id: string): Observable<Consultation> {
    return this.http
      .get<ApiResponse<Consultation>>(`${this.baseUrl}/${id}`)
      .pipe(map((response) => response.data));
  }

  getByPatient(patientId: string): Observable<Consultation[]> {
    return this.http
      .get<ApiResponse<Consultation[]>>(`${this.baseUrl}/patient/${patientId}`)
      .pipe(map((response) => response.data));
  }

  create(payload: ConsultationCreateRequest): Observable<Consultation> {
    return this.http
      .post<ApiResponse<Consultation>>(this.baseUrl, payload)
      .pipe(map((response) => response.data));
  }
}
