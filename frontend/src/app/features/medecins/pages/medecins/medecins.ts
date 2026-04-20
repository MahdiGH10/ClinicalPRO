import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { Medecin, MedecinUpsertRequest } from '../../api/medecin.models';
import { MedecinApiService } from '../../api/medecin-api.service';

@Component({
  selector: 'app-medecins-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './medecins.html',
  styleUrl: './medecins.scss'
})
export class MedecinsComponent implements OnInit {
  private readonly medecinApi = inject(MedecinApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly deletingId = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly medecins = signal<Medecin[]>([]);
  readonly selectedMedecinId = signal<string | null>(null);

  readonly medecinForm = this.fb.group({
    nom: ['', [Validators.required, Validators.maxLength(100)]],
    specialite: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    tel: ['', [Validators.required, Validators.maxLength(30)]]
  });

  readonly isEditMode = computed(() => !!this.selectedMedecinId());

  ngOnInit(): void {
    this.loadMedecins();
  }

  loadMedecins(): void {
    this.loading.set(true);
    this.error.set(null);

    this.medecinApi
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (medecins) => this.medecins.set(medecins),
        error: () => this.error.set('Impossible de charger les médecins depuis l’API.')
      });
  }

  searchByNom(query: string): void {
    const trimmedQuery = query.trim();
    if (!trimmedQuery) {
      this.loadMedecins();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.medecinApi
      .searchByNom(trimmedQuery)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (medecins) => this.medecins.set(medecins),
        error: () => this.error.set('La recherche a échoué. Réessaie.')
      });
  }

  selectForEdit(medecin: Medecin): void {
    this.selectedMedecinId.set(medecin.id);
    this.success.set(null);
    this.error.set(null);

    this.medecinForm.patchValue({
      nom: medecin.nom,
      specialite: medecin.specialite,
      email: medecin.email,
      tel: medecin.tel
    });
  }

  resetForm(): void {
    this.selectedMedecinId.set(null);
    this.medecinForm.reset({
      nom: '',
      specialite: '',
      email: '',
      tel: ''
    });
    this.success.set(null);
    this.error.set(null);
  }

  saveMedecin(): void {
    if (this.medecinForm.invalid || this.saving()) {
      this.medecinForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const payload = this.toPayload();
    const selectedId = this.selectedMedecinId();
    const request$ = selectedId
      ? this.medecinApi.update(selectedId, payload)
      : this.medecinApi.create(payload);

    request$
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: () => {
          this.success.set(selectedId ? 'Médecin mis à jour avec succès.' : 'Médecin créé avec succès.');
          this.resetForm();
          this.loadMedecins();
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible d’enregistrer ce médecin.');
        }
      });
  }

  deleteMedecin(medecin: Medecin): void {
    if (this.deletingId()) {
      return;
    }

    const confirmed = typeof window !== 'undefined'
      ? window.confirm(`Supprimer le médecin ${medecin.nom} ?`)
      : true;

    if (!confirmed) {
      return;
    }

    this.deletingId.set(medecin.id);
    this.error.set(null);
    this.success.set(null);

    this.medecinApi
      .delete(medecin.id)
      .pipe(finalize(() => this.deletingId.set(null)))
      .subscribe({
        next: () => {
          if (this.selectedMedecinId() === medecin.id) {
            this.resetForm();
          }
          this.success.set('Médecin supprimé.');
          this.loadMedecins();
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible de supprimer ce médecin.');
        }
      });
  }

  controlError(name: 'nom' | 'specialite' | 'email' | 'tel'): boolean {
    const control = this.medecinForm.get(name);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private toPayload(): MedecinUpsertRequest {
    const raw = this.medecinForm.getRawValue();

    return {
      nom: raw.nom?.trim() ?? '',
      specialite: raw.specialite?.trim() ?? '',
      email: raw.email?.trim() ?? '',
      tel: raw.tel?.trim() ?? ''
    };
  }
}
