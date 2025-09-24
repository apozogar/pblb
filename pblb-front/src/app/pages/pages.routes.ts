import { Routes } from '@angular/router';
import { Empty } from './empty/empty';
import {CarnetSocioComponent} from "@/pages/carnetSocio/CarnetSocioComponent";

export default [
    { path: 'empty', component: Empty },
    { path: '**', redirectTo: '/notfound' }
] as Routes;
