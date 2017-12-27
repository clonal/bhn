import {Component} from '@angular/core';
import {LoggerService} from '../utils/logger.service';
import {AboutUsService} from './aboutUs.service';

@Component({
    templateUrl: 'aboutUsMenus.component.html'
})

export class AboutUsMenusComponent {
    constructor(private aboutService: AboutUsService, private logger: LoggerService) {}

    deleteMenu(id: number) {
      this.aboutService.deleteMenu(id);
    }
}
