import { Routes } from '@angular/router';

import { LayoutComponent } from './features/layout/layout';
import { FeaturePlaceholderComponent } from './features/layout/pages/feature-placeholder/feature-placeholder';

export const routes: Routes = [
	{
		path: '',
		component: LayoutComponent,
		children: [
			{
				path: '',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Dashboard',
					description: 'Your clean Angular shell is ready. Plug feature slices into this space.'
				}
			},
			{
				path: 'patients',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Patients',
					description: 'This route is reserved for the patients feature slice.'
				}
			},
			{
				path: 'medecins',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Médecins',
					description: 'This route is reserved for the doctors feature slice.'
				}
			},
			{
				path: 'rendezvous',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Rendez-vous',
					description: 'This route is reserved for the appointment feature slice.'
				}
			},
			{
				path: 'consultations',
				component: FeaturePlaceholderComponent,
				data: {
					title: 'Consultations',
					description: 'This route is reserved for the consultation feature slice.'
				}
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
	}
];
