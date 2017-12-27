import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, ParamMap} from '@angular/router';

import 'rxjs/add/operator/switchMap';

import { LoginService } from './login.service';
import { LoggerService } from '../utils/logger.service';

@Component({
    templateUrl: 'activateAccount.component.html'
})

export class ActivateAccountComponent implements OnInit {

    constructor(private logger: LoggerService, private login: LoginService,
                private route: ActivatedRoute, private router: Router) {}

    ngOnInit(): void {
        this.route.paramMap
            .switchMap((params: ParamMap) => this.login.activate(params.get('token')))
            .subscribe(result => this.router.navigate(['login']));
    }
}
