export interface Medecin {
  id: string;
  nom: string;
  specialite: string;
  email: string;
  tel: string;
}

export interface MedecinUpsertRequest {
  nom: string;
  specialite: string;
  email: string;
  tel: string;
}
