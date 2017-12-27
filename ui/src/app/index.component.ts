import {Component} from '@angular/core';
import {Router} from '@angular/router';

import {AuthService} from './auth/auth.service';


@Component({
    templateUrl: 'index.component.html'
})

export class IndexComponent {
    constructor(private auth: AuthService, private router: Router) {}
    getUserHash(): String {
        return this.auth.loadToken();
    }

    onLogout(): void {
        this.auth.clearToken();
        this.router.navigate(['/']);
    }

}
