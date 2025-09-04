import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {MenuItem} from 'primeng/api';
import {AppMenuitem} from './app.menuitem';

@Component({
    selector: 'app-menu',
    standalone: true,
    imports: [CommonModule, AppMenuitem, RouterModule],
    template: `
        <ul class="layout-menu">
            <ng-container *ngFor="let item of model; let i = index">
                <li app-menuitem *ngIf="!item.separator" [item]="item" [index]="i"
                    [root]="true"></li>
                <li *ngIf="item.separator" class="menu-separator"></li>
            </ng-container>
        </ul> `
})
export class AppMenu {
    model: MenuItem[] = [];

    ngOnInit() {

        this.model = [
            {
                label: 'Gestion socios',
                items: [
                    {
                        label: 'Socios',
                        icon: 'pi pi-users',
                        routerLink: ['/socios']
                    },
                    // {
                    //     label: 'Cuotas',
                    //     icon: 'pi pi-money-bill',
                    //     routerLink: ['/cuotas']
                    // }
                    ]
            },
            {
                label: 'Gestion eventos',
                items: [
                    {
                        label: 'Eventos',
                        icon: 'pi pi-calendar',
                        routerLink: ['/eventos']
                    }]
            }
        ]
    };
}
