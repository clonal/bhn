import { Component } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/toPromise';

import { LoginService } from './login.service';
import { LoggerService } from '../utils/logger.service';



@Component({
  templateUrl: 'login.component.html'
})
export class LoginComponent {
    errorMessage: String = null;
    email: String = '';
    password: String = '';
    rememberMe: Boolean = false;

    constructor(private logger: LoggerService, private login: LoginService, private router: Router) {}

    onLogin(): void {
        this.errorMessage = null;
        this.logger.debug('onLogin() acionado');
        if (this.email !== '') {
            this.logger.debug('Existe um userame => ' + this.email);
            this.login.login(this.email, this.password, this.rememberMe)
                .toPromise()
                .then((isLoginOk) => {
                    this.logger.debug('Login OK?=' + isLoginOk);
                    this.router.navigate(['']);
                })
                .catch((error) => {
                    console.log(error);
                    this.email = '';
                    this.password = '';
                    this.rememberMe = false;
                    this.errorMessage = error;
                });
        }
    }
}
