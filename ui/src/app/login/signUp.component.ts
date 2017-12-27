import { Component } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/toPromise';

import { LoggerService } from '../utils/logger.service';
import { LoginService } from './login.service';


@Component({
    templateUrl: 'signUp.component.html'
})

export class SignUpComponent {

    errorMessage: String = null;
    name: String = null;
    email: String = null;
    password: String = null;
    role: number = 0;

    constructor(private logger: LoggerService, private login: LoginService, private router: Router) {}

    onSignUp(): void {
        this.errorMessage = null;
        this.logger.debug('onSignUp');
        this.login.signUp(this.name, this.email, this.password, this.role).toPromise().then((ok) => {
            this.logger.debug('注册成功:' + ok);
            this.router.navigate(['']);
        }).catch((error) => {
            console.log(error);
            this.errorMessage = error;
        });
    }
}
