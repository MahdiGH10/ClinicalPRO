import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';

import { API_BASE_URL, AUTH_STORAGE_KEY } from '../config/api.config';
import { AuthApiResponse, AuthLoginRequest, AuthSession } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sessionSubject = new BehaviorSubject<AuthSession | null>(this.readSession());

  readonly session$ = this.sessionSubject.asObservable();

  login(payload: AuthLoginRequest): Observable<AuthSession> {
    return this.http.post<AuthApiResponse>(`${API_BASE_URL}/auth/login`, payload).pipe(
      map((response) => ({
        token: response.token,
        username: response.username,
        roles: response.roles ?? []
      })),
      tap((session) => this.persistSession(session))
    );
  }

  logout(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(AUTH_STORAGE_KEY);
    }
    this.sessionSubject.next(null);
  }

  getToken(): string | null {
    return this.sessionSubject.value?.token ?? null;
  }

  getSession(): AuthSession | null {
    return this.sessionSubject.value;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  private persistSession(session: AuthSession): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
    }
    this.sessionSubject.next(session);
  }

  private readSession(): AuthSession | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }

    const rawSession = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!rawSession) {
      return null;
    }

    try {
      return JSON.parse(rawSession) as AuthSession;
    } catch {
      localStorage.removeItem(AUTH_STORAGE_KEY);
      return null;
    }
  }
}
