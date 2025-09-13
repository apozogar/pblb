import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {map} from 'rxjs/operators';
import {MessageService} from 'primeng/api';
import {AuthService} from "@/pages/auth/auth.service";

export const adminGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const messageService = inject(MessageService);

    return authService.currentUser.pipe(
        map(user => {
            const isAdmin = user?.authorities?.some(auth => auth.authority === 'ROLE_ADMIN');

            if (isAdmin) {
                return true;
            } else {
                // Opcional: Muestra una notificación al usuario
                messageService.add({
                    severity: 'warn',
                    summary: 'Acceso Denegado',
                    detail: 'No tienes permisos para acceder a esta sección.'
                });

                // Redirige al usuario a una página permitida, como su carnet de socio
                router.navigate(['/carnet-socio']);
                return false;
            }
        })
    );
};
