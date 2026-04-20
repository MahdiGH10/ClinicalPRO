import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth.guard';
import { guestGuard } from './core/auth/guest.guard';
import { LayoutComponent } from './features/layout/layout';
import { LoginComponent } from './features/auth/pages/login/login';
import { DashboardComponent } from './features/dashboard/pages/dashboard/dashboard';
import { PatientsComponent } from './features/patients/pages/patients/patients';
import { MedecinsComponent } from './features/medecins/pages/medecins/medecins';
import { RendezvousComponent } from './features/rendezvous/pages/rendezvous/rendezvous';
import { ConsultationsComponent } from './features/consultations/pages/consultations/consultations';
import { FeaturePlaceholderComponent } from './features/layout/pages/feature-placeholder/feature-placeholder';

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
				path: 'medecins',
				component: MedecinsComponent
			},
			{
				path: 'rendezvous',
				component: RendezvousComponent
			},
			{
				path: 'consultations',
				component: ConsultationsComponent
			},
			{
				path: 'factures',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Factures',
					description: 'This route is reserved for the billing feature slice.'
				}
			},
			{
				path: 'notifications',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Notifications',
					description: 'This route is reserved for reminders and system notifications.'
				}
			}
		]
	},
	{
		path: '**',
		redirectTo: ''
	}
];
