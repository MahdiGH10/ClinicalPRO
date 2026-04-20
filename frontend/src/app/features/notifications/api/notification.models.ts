import { RendezVous } from '../../rendezvous/api/rendezvous.models';

export type StatutNotification = 'EN_ATTENTE' | 'ENVOYEE' | 'ECHEC';
export type TypeNotification = 'RAPPEL' | 'CONFIRMATION' | 'ANNULATION';

export interface Notification {
  id: string;
  rendezVous: RendezVous;
  type: TypeNotification;
  message: string;
  dateEnvoi: string;
  statut: StatutNotification;
}
