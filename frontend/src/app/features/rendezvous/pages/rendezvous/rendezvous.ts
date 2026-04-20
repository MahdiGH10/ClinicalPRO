import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { Medecin } from '../../../medecins/api/medecin.models';
import { MedecinApiService } from '../../../medecins/api/medecin-api.service';
import { Patient } from '../../../patients/api/patient.models';
import { PatientApiService } from '../../../patients/api/patient-api.service';
import { RendezVous, RendezVousUpsertRequest, StatutRendezVous } from '../../api/rendezvous.models';
import { RendezVousApiService } from '../../api/rendezvous-api.service';

@Component({
  selector: 'app-rendezvous-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './rendezvous.html',
  styleUrl: './rendezvous.scss'
})
export class RendezvousComponent implements OnInit {
  private readonly rendezVousApi = inject(RendezVousApiService);
  private readonly patientApi = inject(PatientApiService);
  private readonly medecinApi = inject(MedecinApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly loadingLookups = signal(true);
  readonly saving = signal(false);
  readonly deletingId = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly rendezVous = signal<RendezVous[]>([]);
  readonly patients = signal<Patient[]>([]);
  readonly medecins = signal<Medecin[]>([]);
  readonly selectedRendezVousId = signal<string | null>(null);
  readonly selectedPatientFilter = signal<string>('');

  readonly statutOptions: StatutRendezVous[] = ['EN_ATTENTE', 'CONFIRME', 'ANNULE', 'TERMINE'];

  readonly rendezVousForm = this.fb.group({
    patientId: ['', [Validators.required]],
    medecinId: ['', [Validators.required]],
    dateHeure: ['', [Validators.required]],
    motif: ['', [Validators.required, Validators.maxLength(500)]],
    statut: ['EN_ATTENTE' as StatutRendezVous, [Validators.required]]
  });

  readonly isEditMode = computed(() => !!this.selectedRendezVousId());

  ngOnInit(): void {
    this.loadLookupsAndData();
  }

  loadLookupsAndData(): void {
    this.loadingLookups.set(true);
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      patients: this.patientApi.getAll(),
      medecins: this.medecinApi.getAll(),
      rendezVous: this.rendezVousApi.getAll()
    })
      .pipe(
        finalize(() => {
          this.loadingLookups.set(false);
          this.loading.set(false);
        })
      )
      .subscribe({
        next: ({ patients, medecins, rendezVous }) => {
          this.patients.set(patients);
          this.medecins.set(medecins);
          this.rendezVous.set(rendezVous);
        },
        error: () => this.error.set('Impossible de charger les rendez-vous depuis l’API.')
      });
  }

  loadRendezVous(): void {
    this.loading.set(true);
    this.error.set(null);

    this.rendezVousApi
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (rendezVous) => this.rendezVous.set(rendezVous),
        error: () => this.error.set('Impossible de charger les rendez-vous depuis l’API.')
      });
  }

  searchByPatient(patientId: string): void {
    const trimmedId = patientId.trim();
    this.selectedPatientFilter.set(trimmedId);

    if (!trimmedId) {
      this.loadRendezVous();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.rendezVousApi
      .searchByPatient(trimmedId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (rendezVous) => this.rendezVous.set(rendezVous),
        error: () => this.error.set('La recherche des rendez-vous a échoué. Réessaie.')
      });
  }

  selectForEdit(item: RendezVous): void {
    this.selectedRendezVousId.set(item.id);
    this.success.set(null);
    this.error.set(null);

    this.rendezVousForm.patchValue({
      patientId: item.patientId,
      medecinId: item.medecinId,
      dateHeure: this.toDateTimeLocal(item.dateHeure),
      motif: item.motif,
      statut: item.statut
    });
  }

  resetForm(): void {
    this.selectedRendezVousId.set(null);
    this.rendezVousForm.reset({
      patientId: '',
      medecinId: '',
      dateHeure: '',
      motif: '',
      statut: 'EN_ATTENTE'
    });
    this.success.set(null);
    this.error.set(null);
  }

  saveRendezVous(): void {
    if (this.rendezVousForm.invalid || this.saving()) {
      this.rendezVousForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const payload = this.toPayload();
    const selectedId = this.selectedRendezVousId();
    const request$ = selectedId
      ? this.rendezVousApi.update(selectedId, payload)
      : this.rendezVousApi.create(payload);

    request$
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.success.set(selectedId ? 'Rendez-vous mis à jour avec succès.' : 'Rendez-vous créé avec succès.');
          this.resetForm();
          if (this.selectedPatientFilter()) {
            this.searchByPatient(this.selectedPatientFilter());
          } else {
            this.loadRendezVous();
          }
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible d’enregistrer ce rendez-vous.');
        }
      });
  }

  deleteRendezVous(item: RendezVous): void {
    if (this.deletingId()) {
      return;
    }

    const confirmed = typeof window !== 'undefined'
      ? window.confirm(`Supprimer le rendez-vous de ${item.patientNom} avec ${item.medecinNom} ?`)
      : true;

    if (!confirmed) {
      return;
    }

    this.deletingId.set(item.id);
    this.error.set(null);
    this.success.set(null);

    this.rendezVousApi
      .delete(item.id)
      .pipe(finalize(() => this.deletingId.set(null)))
      .subscribe({
        next: () => {
          if (this.selectedRendezVousId() === item.id) {
            this.resetForm();
          }

          this.success.set('Rendez-vous supprimé.');
          if (this.selectedPatientFilter()) {
            this.searchByPatient(this.selectedPatientFilter());
          } else {
            this.loadRendezVous();
          }
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible de supprimer ce rendez-vous.');
        }
      });
  }

  controlError(name: 'patientId' | 'medecinId' | 'dateHeure' | 'motif' | 'statut'): boolean {
    const control = this.rendezVousForm.get(name);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  statusLabel(statut: StatutRendezVous): string {
    switch (statut) {
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

  private toPayload(): RendezVousUpsertRequest {
    const raw = this.rendezVousForm.getRawValue();

    return {
      patientId: raw.patientId?.trim() ?? '',
      medecinId: raw.medecinId?.trim() ?? '',
      dateHeure: raw.dateHeure ?? '',
      motif: raw.motif?.trim() ?? '',
      statut: (raw.statut ?? 'EN_ATTENTE') as StatutRendezVous
    };
  }

  private toDateTimeLocal(value: string): string {
    if (!value) {
      return '';
    }

    if (value.includes('T') && value.length >= 16) {
      return value.slice(0, 16);
    }

    return value;
  }
}
