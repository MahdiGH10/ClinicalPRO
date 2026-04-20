import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { API_BASE_URL } from '../../../core/config/api.config';
import { ApiResponse } from '../../../shared/api/api-response.model';
import { Notification, StatutNotification } from './notification.models';

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/notifications`;

  getAll(): Observable<Notification[]> {
    return this.http
      .get<ApiResponse<Notification[]>>(this.baseUrl)
      .pipe(map((response) => response.data));
  }

  getById(id: string): Observable<Notification> {
    return this.http
      .get<ApiResponse<Notification>>(`${this.baseUrl}/${id}`)
      .pipe(map((response) => response.data));
  }

  getByRendezVousId(rendezVousId: string): Observable<Notification[]> {
    return this.http
      .get<ApiResponse<Notification[]>>(`${this.baseUrl}/rendezvous/${rendezVousId}`)
      .pipe(map((response) => response.data));
  }

  getByStatut(statut: StatutNotification): Observable<Notification[]> {
    return this.http
      .get<ApiResponse<Notification[]>>(`${this.baseUrl}/statut/${statut}`)
      .pipe(map((response) => response.data));
  }

  sendConfirmation(rendezVousId: string): Observable<Notification> {
    return this.http
      .post<ApiResponse<Notification>>(`${this.baseUrl}/confirmation/${rendezVousId}`, {})
      .pipe(map((response) => response.data));
  }

  sendCancellation(rendezVousId: string): Observable<Notification> {
    return this.http
      .post<ApiResponse<Notification>>(`${this.baseUrl}/annulation/${rendezVousId}`, {})
      .pipe(map((response) => response.data));
  }

  sendReminder(rendezVousId: string): Observable<Notification> {
    return this.http
      .post<ApiResponse<Notification>>(`${this.baseUrl}/rappel/${rendezVousId}`, {})
      .pipe(map((response) => response.data));
  }

  sendDailyReminders(date?: string): Observable<Notification[]> {
    const params = date ? { date } : undefined;
    return this.http
      .post<ApiResponse<Notification[]>>(`${this.baseUrl}/rappels`, {}, { params })
      .pipe(map((response) => response.data));
  }
}
