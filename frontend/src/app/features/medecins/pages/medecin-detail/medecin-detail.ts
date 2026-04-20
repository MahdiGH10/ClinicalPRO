import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { Medecin } from '../../api/medecin.models';
import { MedecinApiService } from '../../api/medecin-api.service';

@Component({
  selector: 'app-medecin-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './medecin-detail.html',
  styleUrl: './medecin-detail.scss'
})
export class MedecinDetailComponent implements OnInit {
  private readonly medecinApi = inject(MedecinApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly medecin = signal<Medecin | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadMedecin(id);
    }
  }

  loadMedecin(id: string): void {
    this.loading.set(true);
    this.error.set(null);

    this.medecinApi
      .getById(id)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (medecin) => this.medecin.set(medecin),
        error: () => {
          this.error.set('Médecin non trouvé.');
          setTimeout(() => this.router.navigate(['/medecins']), 2000);
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/medecins']);
  }
}
