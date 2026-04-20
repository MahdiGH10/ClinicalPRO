import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth.guard';
import { guestGuard } from './core/auth/guest.guard';
import { LayoutComponent } from './features/layout/layout';
import { LoginComponent } from './features/auth/pages/login/login';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard';
import { PatientsComponent } from './features/patients/pages/patients/patients';
import { PatientDetailComponent } from './features/patients/pages/patient-detail/patient-detail';
import { MedecinsComponent } from './features/medecins/pages/medecins/medecins';
import { MedecinDetailComponent } from './features/medecins/pages/medecin-detail/medecin-detail';
import { RendezvousComponent } from './features/rendezvous/pages/rendezvous/rendezvous';
import { RendezvousDetailComponent } from './features/rendezvous/pages/rendezvous-detail/rendezvous-detail';
import { FacturesComponent } from './features/factures/pages/factures/factures';
import { ConsultationsComponent } from './features/consultations/pages/consultations/consultations';
import { NotificationsComponent } from './features/notifications/pages/notifications/notifications';

export const routes: Routes = [
	{
		path: 'auth/login',
		component: LoginComponent,
		canActivate: [guestGuard]
	},
	{
		path: '',
		component: LayoutComponent,
		canActivate: [authGuard],
		children: [
			{
				path: '',
				component: DashboardComponent
			},
			{
				path: 'patients',
				component: PatientsComponent
			},
			{
				path: 'patients/:id',
				component: PatientDetailComponent
			},
			{
				path: 'medecins',
				component: MedecinsComponent
			},
			{
				path: 'medecins/:id',
				component: MedecinDetailComponent
			},
			{
				path: 'rendezvous',
				component: RendezvousComponent
			},
			{
				path: 'rendezvous/:id',
				component: RendezvousDetailComponent
			},
			{
				path: 'consultations',
				component: ConsultationsComponent
			},
			{
				path: 'factures',
				component: FacturesComponent
			},
			{
				path: 'notifications',
				component: NotificationsComponent
			}
		]
	},
	{
		path: '**',
		redirectTo: ''
	}
];
