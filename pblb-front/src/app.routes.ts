import { Routes } from '@angular/router';
import { AppLayout } from '@//layout/component/app.layout';
import { Landing } from '@//pages/landing/landing';
import { Notfound } from '@//pages/notfound/notfound';
import { CarnetSocioComponent } from "@/pages/carnetSocio/CarnetSocioComponent";
import { adminGuard } from '@/guards/admin.guard';
import { authGuard } from '@/guards/auth.guard';

export const appRoutes: Routes = [
    {
        path: '',
        component: AppLayout,
        canActivate: [authGuard],
        children: [
            { path: '', redirectTo: 'socios', pathMatch: 'full' },
            {
                path: 'socios',
                loadComponent: () =>
                    import('@/pages/socios/SociosComponent').then(m => m.SociosComponent),
                canActivate: [adminGuard]
            },
            // {
            //     path: 'eventos',
            //     loadComponent: () =>
            //         import('@/pages/eventos/EventosComponent').then(m => m.EventosComponent)
            // },
            // {
            //     path: 'cuotas',
            //     loadComponent: () =>
            //         import('@/pages/cuotas/CuotasComponet').then(m => m.CuotasComponet)
            // },
            // {
            //     path: 'informes',
            //     loadComponent: () =>
            //         import('@/pages/informes/InformesComponent').then(m => m.InformesComponent)
            // },
            {
                path: 'carnet-socio',
                component: CarnetSocioComponent
            },
        ]
    },
    {
        path: 'landing', component:
            Landing
    }
    ,
    {
        path: 'notfound', component:
            Notfound
    }
    ,
    {
        path: 'auth', loadChildren:
            () => import('@/pages/auth/auth.routes')
    }
    ,
    {
        path: '**', redirectTo:
            '/notfound'
    }
];
