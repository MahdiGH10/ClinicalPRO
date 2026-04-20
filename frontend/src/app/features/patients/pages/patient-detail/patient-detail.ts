import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { Patient } from '../../api/patient.models';
import { PatientApiService } from '../../api/patient-api.service';

@Component({
  selector: 'app-patient-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.scss'
})
export class PatientDetailComponent implements OnInit {
  private readonly patientApi = inject(PatientApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly patient = signal<Patient | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPatient(id);
    }
  }

  loadPatient(id: string): void {
    this.loading.set(true);
    this.error.set(null);

    this.patientApi
      .getById(id)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (patient) => this.patient.set(patient),
        error: () => {
          this.error.set('Patient non trouvé.');
          setTimeout(() => this.router.navigate(['/patients']), 2000);
        }
      });
  }

  goBack(): void {
    this.router.navigate(['/patients']);
  }

  formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleDateString('fr-FR');
  }
}
