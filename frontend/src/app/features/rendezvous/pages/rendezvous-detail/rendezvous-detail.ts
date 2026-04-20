import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { RendezVous } from '../../api/rendezvous.models';
import { RendezVousApiService } from '../../api/rendezvous-api.service';

@Component({
  selector: 'app-rendezvous-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rendezvous-detail.html',
  styleUrl: './rendezvous-detail.scss'
})
export class RendezvousDetailComponent implements OnInit {
  private readonly rendezVousApi = inject(RendezVousApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly rendezVous = signal<RendezVous | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadRendezVous(id);
    }
  }

  loadRendezVous(id: string): void {
    this.loading.set(true);
    this.error.set(null);

    this.rendezVousApi
      .getById(id)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (rv) => this.rendezVous.set(rv),
        error: () => {
          this.error.set('Rendez-vous non trouvé.');
          setTimeout(() => this.router.navigate(['/rendezvous']), 2000);
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/rendezvous']);
  }

  formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString('fr-FR', {
      dateStyle: 'full',
      timeStyle: 'short'
    });
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'CONFIRME':
        return 'Confirmé';
      case 'ANNULE':
        return 'Annulé';
      case 'TERMINE':
        return 'Terminé';
      default:
        return 'En attente';
    }
  }

  statusClass(status: string): string {
    switch (status) {
      case 'CONFIRME':
        return 'status-confirmed';
      case 'ANNULE':
        return 'status-canceled';
      case 'TERMINE':
        return 'status-completed';
      default:
        return 'status-pending';
    }
  }
}
