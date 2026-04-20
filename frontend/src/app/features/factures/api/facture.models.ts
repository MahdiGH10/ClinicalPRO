import { Consultation } from '../../consultations/api/consultation.models';

export type StatutPaiement = 'EN_ATTENTE' | 'PAYEE' | 'ANNULEE';

export interface Facture {
  id: string;
  numeroFacture: string;
  consultation: Consultation;
  montant: number;
  dateEmission: string;
  statutPaiement: StatutPaiement;
}
