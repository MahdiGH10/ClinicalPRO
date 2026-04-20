import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isLoginRequest = req.url.includes('/auth/login');
  const token = authService.getToken();
  const authorizedRequest = !isLoginRequest && token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authorizedRequest).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401 && !isLoginRequest) {
        const currentUrl = router.url || '/';
        authService.logout();
        router.navigate(['/auth/login'], {
          queryParams: {
            reason: 'session-expired',
            returnUrl: currentUrl
          }
        });
      }

      return throwError(() => error);
    })
  );
};
