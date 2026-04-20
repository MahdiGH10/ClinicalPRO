import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { finalize } from 'rxjs/operators';

import { Facture, StatutPaiement } from '../../api/facture.models';
import { FactureApiService } from '../../api/facture-api.service';

@Component({
  selector: 'app-factures-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './factures.html',
  styleUrl: './factures.scss'
})
export class FacturesComponent implements OnInit {
  private readonly factureApi = inject(FactureApiService);

  readonly loading = signal(true);
  readonly payingId = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly factures = signal<Facture[]>([]);
  readonly selectedStatusFilter = signal<StatutPaiement | ''>('');

  readonly statutOptions: (StatutPaiement | '')[] = ['', 'EN_ATTENTE', 'PAYEE', 'ANNULEE'];

  ngOnInit(): void {
    this.loadFactures();
  }

  loadFactures(): void {
    this.loading.set(true);
    this.error.set(null);

    this.factureApi
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (factures) => this.factures.set(factures),
        error: () => this.error.set('Impossible de charger les factures depuis l\'API.')
      });
  }

  filterByStatus(status: StatutPaiement | ''): void {
    this.selectedStatusFilter.set(status);
  }

  filteredFactures(): Facture[] {
    const status = this.selectedStatusFilter();
    if (!status) {
      return this.factures();
    }

    return this.factures().filter((f) => f.statutPaiement === status);
  }

  markAsPaid(facture: Facture): void {
    if (this.payingId()) {
      return;
    }

    if (facture.statutPaiement === 'PAYEE') {
      this.error.set('Cette facture est déjà marquée comme payée.');
      return;
    }

    const confirmed = typeof window !== 'undefined'
      ? window.confirm(`Marquer la facture ${facture.numeroFacture} comme payée ?`)
      : true;

    if (!confirmed) {
      return;
    }

    this.payingId.set(facture.id);
    this.error.set(null);
    this.success.set(null);

    this.factureApi
      .markPaid(facture.id)
      .pipe(finalize(() => this.payingId.set(null)))
      .subscribe({
        next: () => {
          this.success.set(`Facture ${facture.numeroFacture} marquée comme payée.`);
          this.loadFactures();
        },
        error: (err: unknown) => {
          const backendMessage = (err as { error?: { message?: string } })?.error?.message;
          this.error.set(backendMessage || 'Impossible de marquer cette facture comme payée.');
        }
      });
  }

  statutLabel(statut: StatutPaiement): string {
    switch (statut) {
      case 'PAYEE':
        return 'Payée';
      case 'ANNULEE':
        return 'Annulée';
      default:
        return 'En attente';
    }
  }

  statutClass(statut: StatutPaiement): string {
    switch (statut) {
      case 'PAYEE':
        return 'status-paid';
      case 'ANNULEE':
        return 'status-canceled';
      default:
        return 'status-pending';
    }
  }

  formatDate(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleDateString('fr-FR');
  }
}

