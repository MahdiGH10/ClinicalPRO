import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';

import { ConsultationApiService } from '../../../consultations/api/consultation-api.service';
import { FactureApiService } from '../../../factures/api/facture-api.service';
import { MedecinApiService } from '../../../medecins/api/medecin-api.service';
import { NotificationApiService } from '../../../notifications/api/notification-api.service';
import { PatientApiService } from '../../../patients/api/patient-api.service';
import { RendezVous, StatutRendezVous } from '../../../rendezvous/api/rendezvous.models';
import { RendezVousApiService } from '../../../rendezvous/api/rendezvous-api.service';
import { Notification } from '../../../notifications/api/notification.models';

interface DashboardStats {
  patients: number;
  medecins: number;
  consultations: number;
  rendezVousTotal: number;
  rendezVousToday: number;
  rendezVousConfirmed: number;
  facturesPending: number;
  notificationsSent: number;
}

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  private readonly patientApi = inject(PatientApiService);
  private readonly medecinApi = inject(MedecinApiService);
  private readonly rendezVousApi = inject(RendezVousApiService);
  private readonly consultationApi = inject(ConsultationApiService);
  private readonly factureApi = inject(FactureApiService);
  private readonly notificationApi = inject(NotificationApiService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly lastUpdated = signal<Date | null>(null);

  readonly stats = signal<DashboardStats>({
    patients: 0,
    medecins: 0,
    consultations: 0,
    rendezVousTotal: 0,
    rendezVousToday: 0,
    rendezVousConfirmed: 0,
    facturesPending: 0,
    notificationsSent: 0
  });

  readonly upcomingRendezVous = signal<RendezVous[]>([]);
  readonly latestNotifications = signal<Notification[]>([]);

  ngOnInit(): void {
    this.loadDashboard();
  }

  refresh(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      patients: this.patientApi.getAll(),
      medecins: this.medecinApi.getAll(),
      rendezVous: this.rendezVousApi.getAll(),
      consultations: this.consultationApi.getAll(),
      factures: this.factureApi.getAll(),
      notifications: this.notificationApi.getAll()
    }).subscribe({
      next: ({ patients, medecins, rendezVous, consultations, factures, notifications }) => {
        const now = new Date();
        const today = now.toDateString();

        const sortedUpcoming = [...rendezVous]
          .filter((rdv) => new Date(rdv.dateHeure).getTime() >= now.getTime())
          .sort((a, b) => new Date(a.dateHeure).getTime() - new Date(b.dateHeure).getTime())
          .slice(0, 5);

        const sortedNotifications = [...notifications]
          .sort((a, b) => new Date(b.dateEnvoi).getTime() - new Date(a.dateEnvoi).getTime())
          .slice(0, 5);

        this.stats.set({
          patients: patients.length,
          medecins: medecins.length,
          consultations: consultations.length,
          rendezVousTotal: rendezVous.length,
          rendezVousToday: rendezVous.filter((rdv) => new Date(rdv.dateHeure).toDateString() === today).length,
          rendezVousConfirmed: rendezVous.filter((rdv) => rdv.statut === 'CONFIRME').length,
          facturesPending: factures.filter((facture) => facture.statutPaiement === 'EN_ATTENTE').length,
          notificationsSent: notifications.filter((notification) => notification.statut === 'ENVOYEE').length
        });

        this.upcomingRendezVous.set(sortedUpcoming);
        this.latestNotifications.set(sortedNotifications);
        this.lastUpdated.set(new Date());
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger le dashboard depuis l’API. Vérifie que le backend tourne bien.');
        this.loading.set(false);
      }
    });
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
}
