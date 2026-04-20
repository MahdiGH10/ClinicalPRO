import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { finalize } from 'rxjs/operators';

import { Notification, StatutNotification, TypeNotification } from '../../api/notification.models';
import { NotificationApiService } from '../../api/notification-api.service';

@Component({
  selector: 'app-notifications-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss'
})
export class NotificationsComponent implements OnInit {
  private readonly notificationApi = inject(NotificationApiService);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly notifications = signal<Notification[]>([]);
  readonly selectedTypeFilter = signal<TypeNotification | ''>('');
  readonly selectedStatusFilter = signal<StatutNotification | ''>('');

  readonly typeOptions: (TypeNotification | '')[] = ['', 'RAPPEL', 'CONFIRMATION', 'ANNULATION'];
  readonly statusOptions: (StatutNotification | '')[] = ['', 'EN_ATTENTE', 'ENVOYEE', 'ECHEC'];

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading.set(true);
    this.error.set(null);

    this.notificationApi
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (notifications) => this.notifications.set(notifications),
        error: () => this.error.set('Impossible de charger les notifications depuis l\'API.')
      });
  }

  filterByType(type: TypeNotification | ''): void {
    this.selectedTypeFilter.set(type);
  }

  filterByStatus(status: StatutNotification | ''): void {
    this.selectedStatusFilter.set(status);
  }

  filteredNotifications(): Notification[] {
    let filtered = this.notifications();

    const type = this.selectedTypeFilter();
    if (type) {
      filtered = filtered.filter((n) => n.type === type);
    }

    const status = this.selectedStatusFilter();
    if (status) {
      filtered = filtered.filter((n) => n.statut === status);
    }

    return filtered;
  }

  typeLabel(type: TypeNotification): string {
    switch (type) {
      case 'CONFIRMATION':
        return 'Confirmation';
      case 'ANNULATION':
        return 'Annulation';
      default:
        return 'Rappel';
    }
  }

  typeClass(type: TypeNotification): string {
    switch (type) {
      case 'CONFIRMATION':
        return 'type-confirmation';
      case 'ANNULATION':
        return 'type-cancellation';
      default:
        return 'type-reminder';
    }
  }

  statusLabel(status: StatutNotification): string {
    switch (status) {
      case 'ENVOYEE':
        return 'Envoyée';
      case 'ECHEC':
        return 'Échec';
      default:
        return 'En attente';
    }
  }

  statusClass(status: StatutNotification): string {
    switch (status) {
      case 'ENVOYEE':
        return 'status-sent';
      case 'ECHEC':
        return 'status-failed';
      default:
        return 'status-pending';
    }
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
}
