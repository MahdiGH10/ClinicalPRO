export interface Patient {
  id: string;
  nom: string;
  dateNaissance: string;
  dossierMedical?: string;
  tel: string;
}

export interface PatientUpsertRequest {
  nom: string;
  dateNaissance: string;
  dossierMedical?: string;
  tel: string;
}
