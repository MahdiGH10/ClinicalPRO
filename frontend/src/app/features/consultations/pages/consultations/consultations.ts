import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { Patient } from '../../../patients/api/patient.models';
import { PatientApiService } from '../../../patients/api/patient-api.service';
import { RendezVous } from '../../../rendezvous/api/rendezvous.models';
import { RendezVousApiService } from '../../../rendezvous/api/rendezvous-api.service';
import { Consultation, ConsultationCreateRequest } from '../../api/consultation.models';
import { ConsultationApiService } from '../../api/consultation-api.service';

@Component({
  selector: 'app-consultations-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './consultations.html',
  styleUrl: './consultations.scss'
})
export class ConsultationsComponent implements OnInit {
  private readonly consultationApi = inject(ConsultationApiService);
  private readonly patientApi = inject(PatientApiService);
  private readonly rendezVousApi = inject(RendezVousApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly loadingLookups = signal(true);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly consultations = signal<Consultation[]>([]);
  readonly patients = signal<Patient[]>([]);
  readonly rendezVous = signal<RendezVous[]>([]);
  readonly selectedPatientFilter = signal<string>('');

  readonly consultationForm = this.fb.group({
    rendezVousId: ['', [Validators.required]],
    prix: [null as number | null, [Validators.required, Validators.min(0)]],
    diagnostic: ['', [Validators.maxLength(2000)]],
    ordonnance: ['', [Validators.maxLength(2000)]]
  });

  readonly filteredRendezVousOptions = computed(() => {
    const selectedPatientId = this.selectedPatientFilter();
    if (!selectedPatientId) {
      return this.rendezVous();
    }

    return this.rendezVous().filter((item) => item.patientId === selectedPatientId);
  });

  ngOnInit(): void {
    this.loadLookupsAndConsultations();
  }

  loadLookupsAndConsultations(): void {
    this.loadingLookups.set(true);
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      patients: this.patientApi.getAll(),
      rendezVous: this.rendezVousApi.getAll(),
      consultations: this.consultationApi.getAll()
    })
      .pipe(
        finalize(() => {
          this.loadingLookups.set(false);
          this.loading.set(false);
        })
      )
      .subscribe({
        next: ({ patients, rendezVous, consultations }) => {
          this.patients.set(patients);
          this.rendezVous.set(rendezVous);
          this.consultations.set(consultations);
        },
        error: () => this.error.set('Impossible de charger les consultations depuis l’API.')
      });
  }

  loadConsultations(): void {
    this.loading.set(true);
    this.error.set(null);

    this.consultationApi
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (consultations) => this.consultations.set(consultations),
        error: () => this.error.set('Impossible de charger les consultations depuis l’API.')
      });
  }

  searchByPatient(patientId: string): void {
    const trimmedId = patientId.trim();
    this.selectedPatientFilter.set(trimmedId);

    if (!trimmedId) {
      this.loadConsultations();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.consultationApi
      .getByPatient(trimmedId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (consultations) => this.consultations.set(consultations),
        error: () => this.error.set('La recherche des consultations a échoué. Réessaie.')
      });
  }

  saveConsultation(): void {
    if (this.consultationForm.invalid || this.saving()) {
      this.consultationForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const payload = this.toPayload();

    this.consultationApi
      .create(payload)
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.success.set('Consultation créée avec succès.');
          this.consultationForm.reset({
            rendezVousId: '',
            prix: null,
            diagnostic: '',
            ordonnance: ''
          });

          if (this.selectedPatientFilter()) {
            this.searchByPatient(this.selectedPatientFilter());
          } else {
            this.loadConsultations();
          }
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible de créer cette consultation.');
        }
      });
  }

  controlError(name: 'rendezVousId' | 'prix' | 'diagnostic' | 'ordonnance'): boolean {
    const control = this.consultationForm.get(name);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  rendezVousLabel(item: RendezVous): string {
    return `${item.patientNom} • ${item.medecinNom} • ${this.formatDate(item.dateHeure)}`;
  }

  formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString('fr-FR', {
      dateStyle: 'medium',
      timeStyle: 'short'
    });
  }

  private toPayload(): ConsultationCreateRequest {
    const raw = this.consultationForm.getRawValue();

    return {
      rendezVousId: raw.rendezVousId?.trim() ?? '',
      prix: Number(raw.prix ?? 0),
      diagnostic: raw.diagnostic?.trim() || undefined,
      ordonnance: raw.ordonnance?.trim() || undefined
    };
  }
}
