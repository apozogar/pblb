import {Routes} from '@angular/router';
import {AppLayout} from '@//layout/component/app.layout';
import {Dashboard} from '@//pages/dashboard/dashboard';
import {Documentation} from '@//pages/documentation/documentation';
import {Landing} from '@//pages/landing/landing';
import {Notfound} from '@//pages/notfound/notfound';

export const appRoutes: Routes = [
    {
        path: '',
        component: AppLayout,
        children: [
            {path: '', component: Dashboard},
            {path: 'uikit', loadChildren: () => import('@/pages/uikit/uikit.routes')},
            {path: 'documentation', component: Documentation},
            {path: 'pages', loadChildren: () => import('@/pages/pages.routes')},
            {
                path: 'socios',
                loadComponent: () =>
                    import('@/pages/socios/SociosComponent').then(m => m.SociosComponent)
            },
            {
                path: 'eventos',
                loadComponent: () =>
                    import('@/pages/eventos/EventosComponent').then(m => m.EventosComponent)
            },
            {
                path: 'cuotas',
                loadComponent: () =>
                    import('@/pages/cuotas/CuotasComponet').then(m => m.CuotasComponet)
            },
            {
                path: 'informes',
                loadComponent: () =>
                    import('@/pages/informes/InformesComponent').then(m => m.InformesComponent)
            }
        ]
    },
    {path: 'landing', component: Landing},
    {path: 'notfound', component: Notfound},
    {path: 'auth', loadChildren: () => import('@/pages/auth/auth.routes')},
    {path: '**', redirectTo: '/notfound'}
];
