export type StatutRendezVous = 'EN_ATTENTE' | 'CONFIRME' | 'ANNULE' | 'TERMINE';

export interface RendezVous {
  id: string;
  patientId: string;
  patientNom: string;
  medecinId: string;
  medecinNom: string;
  dateHeure: string;
  motif: string;
  statut: StatutRendezVous;
}

export interface RendezVousUpsertRequest {
  patientId: string;
  medecinId: string;
  dateHeure: string;
  motif: string;
  statut: StatutRendezVous;
}
