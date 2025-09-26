import { Component } from '@angular/core';

@Component({
    standalone: true,
    selector: 'app-footer',
    template: `
        <div class="layout-footer">
            Peña Bética Luis Bellver by
            <a href="" target="_blank" rel="noopener noreferrer"
               class="text-primary font-bold hover:underline">Softwells</a>
        </div>`
})
export class AppFooter {}
