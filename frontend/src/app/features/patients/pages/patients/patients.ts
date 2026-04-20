import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { Patient, PatientUpsertRequest } from '../../api/patient.models';
import { PatientApiService } from '../../api/patient-api.service';

@Component({
  selector: 'app-patients-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './patients.html',
  styleUrl: './patients.scss'
})
export class PatientsComponent implements OnInit {
  private readonly patientApi = inject(PatientApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly deletingId = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly patients = signal<Patient[]>([]);
  readonly selectedPatientId = signal<string | null>(null);

  readonly patientForm = this.fb.group({
    nom: ['', [Validators.required, Validators.maxLength(100)]],
    dateNaissance: ['', [Validators.required]],
    dossierMedical: ['', [Validators.maxLength(1000)]],
    tel: ['', [Validators.required, Validators.maxLength(30)]]
  });

  readonly isEditMode = computed(() => !!this.selectedPatientId());

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loading.set(true);
    this.error.set(null);

    this.patientApi
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (patients) => this.patients.set(patients),
        error: () => this.error.set('Impossible de charger les patients depuis l’API.')
      });
  }

  searchByNom(query: string): void {
    const trimmedQuery = query.trim();
    if (!trimmedQuery) {
      this.loadPatients();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.patientApi
      .searchByNom(trimmedQuery)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (patients) => this.patients.set(patients),
        error: () => this.error.set('La recherche a échoué. Réessaie.')
      });
  }

  selectForEdit(patient: Patient): void {
    this.selectedPatientId.set(patient.id);
    this.success.set(null);
    this.error.set(null);

    this.patientForm.patchValue({
      nom: patient.nom,
      dateNaissance: patient.dateNaissance,
      dossierMedical: patient.dossierMedical ?? '',
      tel: patient.tel
    });
  }

  resetForm(): void {
    this.selectedPatientId.set(null);
    this.patientForm.reset({
      nom: '',
      dateNaissance: '',
      dossierMedical: '',
      tel: ''
    });
    this.success.set(null);
    this.error.set(null);
  }

  savePatient(): void {
    if (this.patientForm.invalid || this.saving()) {
      this.patientForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const payload = this.toPayload();
    const selectedId = this.selectedPatientId();
    const request$ = selectedId
      ? this.patientApi.update(selectedId, payload)
      : this.patientApi.create(payload);

    request$
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.success.set(selectedId ? 'Patient mis à jour avec succès.' : 'Patient créé avec succès.');
          this.resetForm();
          this.loadPatients();
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible d’enregistrer ce patient.');
        }
      });
  }

  deletePatient(patient: Patient): void {
    if (this.deletingId()) {
      return;
    }

    const confirmed = typeof window !== 'undefined'
      ? window.confirm(`Supprimer le patient ${patient.nom} ?`)
      : true;

    if (!confirmed) {
      return;
    }

    this.deletingId.set(patient.id);
    this.error.set(null);
    this.success.set(null);

    this.patientApi
      .delete(patient.id)
      .pipe(finalize(() => this.deletingId.set(null)))
      .subscribe({
        next: () => {
          if (this.selectedPatientId() === patient.id) {
            this.resetForm();
          }
          this.success.set('Patient supprimé.');
          this.loadPatients();
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible de supprimer ce patient.');
        }
      });
  }

  controlError(name: 'nom' | 'dateNaissance' | 'dossierMedical' | 'tel'): boolean {
    const control = this.patientForm.get(name);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private toPayload(): PatientUpsertRequest {
    const raw = this.patientForm.getRawValue();

    return {
      nom: raw.nom?.trim() ?? '',
      dateNaissance: raw.dateNaissance ?? '',
      dossierMedical: raw.dossierMedical?.trim() || undefined,
      tel: raw.tel?.trim() ?? ''
    };
  }
}
