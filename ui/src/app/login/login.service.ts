import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import {AuthService} from '../auth/auth.service';
import {LoggerService} from '../utils/logger.service';

export const TOKEN_NAME = 'token';

@Injectable()
export class LoginService {

    constructor(private logger: LoggerService, private auth: AuthService, private http: HttpClient) {}

    login(email: String, password: String, rememberMe: Boolean): Observable<boolean> {
        return this.http.post('/api/auth/signin', {email: email, password: password, rememberMe: rememberMe})
            .map((result) => {
                this.auth.clearToken();
                let error = result['error'];
                if (typeof(error) === 'undefined') {
                    let token = result['token'];
                    this.logger.debug('Received auth token => ' + token);
                    let isAuthenticated = (typeof(token) !== 'undefined');
                    if (isAuthenticated) {
                        this.auth.saveToken(token);
                    }
                    this.logger.debug('User [' + email + '] is authenticated ? => ' + isAuthenticated);
                    return isAuthenticated;
                } else {
                    this.logger.debug('login error: ' + error);
                    return false;
                }
            });
    }

    signUp(name: String, email: String, password: String, role: number): Observable<boolean> {
        return this.http.post('/api/auth/signup', {name: name, email: email, password: password, role: role})
            .map((result) => {
                this.logger.debug('注册完成否:' + result);
                return result === true;
            });
    }

    activate(token: String): Observable<Boolean> {
        return this.http.post('/api/auth/activate/' + token, null)
            .map((result) => {
                let error = result['error'];
                if (error) {
                    this.logger.debug('激活错误:' + error);
                } else {
                    this.logger.debug('激活完成否:' + result['result']);
                }
                return error;
            });
    }
}
