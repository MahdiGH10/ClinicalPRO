import { RendezVous, StatutRendezVous } from '../../rendezvous/api/rendezvous.models';

export interface Consultation {
  id: string;
  rendezVous: RendezVous;
  diagnostic?: string;
  ordonnance?: string;
  prix: number;
}

export interface ConsultationCreateRequest {
  rendezVousId: string;
  diagnostic?: string;
  ordonnance?: string;
  prix: number;
}

export interface ConsultationRendezVousSummary {
  id: string;
  patientId: string;
  patientNom: string;
  medecinId: string;
  medecinNom: string;
  dateHeure: string;
  motif: string;
  statut: StatutRendezVous;
}
