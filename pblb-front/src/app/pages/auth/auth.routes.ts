import {Routes} from '@angular/router';
import {Access} from './access';
import {Error} from './error';
import {ForgotPassword} from './forgot-password';
import {ResetPassword} from './reset-password';
import {LoginComponent} from "@/pages/auth/login/login.component";
import {RegisterComponent} from "@/pages/auth/register/register.component";

export default [
    {path: 'access', component: Access},
    {path: 'error', component: Error},
    {path: 'login', component: LoginComponent},
    {path: 'forgot-password', component: ForgotPassword},
    {path: 'reset-password', component: ResetPassword},
    {path: 'register', component: RegisterComponent}
] as Routes;
