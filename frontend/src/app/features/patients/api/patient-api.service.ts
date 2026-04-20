import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiResponse } from '../../../shared/api/api-response.model';
import { Patient, PatientUpsertRequest } from './patient.models';

@Injectable({ providedIn: 'root' })
export class PatientApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/patients`;

  getAll(): Observable<Patient[]> {
    return this.http.get<ApiResponse<Patient[]>>(this.baseUrl).pipe(map((response) => response.data));
  }

  getById(id: string): Observable<Patient> {
    return this.http.get<ApiResponse<Patient>>(`${this.baseUrl}/${id}`).pipe(map((response) => response.data));
  }

  searchByNom(nom: string): Observable<Patient[]> {
    return this.http
      .get<ApiResponse<Patient[]>>(`${this.baseUrl}/search`, { params: { nom } })
      .pipe(map((response) => response.data));
  }

  create(payload: PatientUpsertRequest): Observable<Patient> {
    return this.http
      .post<ApiResponse<Patient>>(this.baseUrl, payload)
      .pipe(map((response) => response.data));
  }

  update(id: string, payload: PatientUpsertRequest): Observable<Patient> {
    return this.http
      .put<ApiResponse<Patient>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map((response) => response.data));
  }

  delete(id: string): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => void 0));
  }
}
