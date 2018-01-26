import {Component, OnInit} from '@angular/core';

import {AuthService} from './auth/auth.service';
import {BackendService} from './backend/backend.service';
import {LoggerService} from './utils/logger.service';
import {Menu} from "./model/menu";

@Component({
  selector: 'my-app',
  templateUrl: 'app.component.html'
})
export class AppComponent implements OnInit {
    // menus: Menu[];
    constructor(private auth: AuthService, private backService: BackendService, private logger: LoggerService) {}

    getUserHash(): String {
        return this.auth.loadToken();
    }

    ngOnInit(): void {
        this.backService.initMenus();
    }
}
