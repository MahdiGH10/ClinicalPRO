import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly sessionNotice = this.route.snapshot.queryParamMap.get('reason') === 'session-expired'
    ? 'Votre session a expiré. Reconnectez-vous pour continuer.'
    : null;

  readonly loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  submit(): void {
    if (this.loginForm.invalid || this.loading()) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/';
    const { username, password } = this.loginForm.getRawValue();

    this.authService
      .login({ username: username ?? '', password: password ?? '' })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.router.navigateByUrl(returnUrl);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getFriendlyMessage(error));
        }
      });
  }

  controlError(name: 'username' | 'password'): boolean {
    const control = this.loginForm.get(name);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private getFriendlyMessage(error: unknown): string {
    const status = (error as { status?: number })?.status;
    const backendMessage = (error as { error?: { message?: string } })?.error?.message;

    if (status === 401) {
      return 'Identifiants invalides. Vérifie ton nom d’utilisateur et ton mot de passe.';
    }

    return backendMessage || 'Impossible de se connecter pour le moment. Réessaie dans un instant.';
  }
}
