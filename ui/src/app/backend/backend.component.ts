import {Component, OnInit} from '@angular/core';
import {LoggerService} from '../utils/logger.service';
import {BackendService} from './backend.service';
import {Menu} from '../model/menu';
import {Banner} from '../model/banner';


@Component({
    templateUrl: 'backend/backend.component.html'
})

export class BackendComponent implements OnInit {
    // menus: Menu[];
    banners: Banner[]; // 是否用map
    selectedMenu = 1;
    constructor(private logger: LoggerService, private backService: BackendService) {}

    ngOnInit(): void {
/*        this.backService.getMenus().subscribe(result => {
            this.menus = result; this.logger.debug('menus: ' + this.menus.length)}
        );*/
        this.backService.getBanners().subscribe(result => {
            this.banners = result;
            this.logger.debug('banners:' + this.banners.length + ', ' + this.banners[0]);
        }
        );
    }

    onSelect(menuId: number): void {
        this.logger.debug('click menu ' + menuId);
    }

    deleteImage(order: number): void {
        this.backService.deleteRootBanner(order, this.selectedMenu).toPromise().then((result) => {
                if (result) {
                    alert('deleteImage1 ' + result);
                    let index = this.banners.findIndex(b => b.order === order);
                    this.banners.splice(index, 1);
                }
            }
        );
    }

    editImage(order: number): void {
        this.backService.editRootBanner(order, this.selectedMenu).toPromise().then((result) => {
                alert('editImage ' + result);
            }
        );
    }
}
