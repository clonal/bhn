import {Component, Input} from '@angular/core';
import {Menu} from './model/menu';
import {BackendService} from './backend.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-tree',
    templateUrl: './tree-menu.component.html',
})
export class TreeMenuComponent {
    // 超简单, 重点: 接收上级的值
    // 可以为树建立一个接口, 这里简化为any
    @Input() menus: Menu[];
    @Input() prefix: string = '';
    constructor(private backService: BackendService, private router: Router) {}
    itemClick(id: number, name: string) {
        // alert(this.backService.topRouter(name));
        // this.router.navigate([this.backService.topRouter(name)]);
    }

    menuRouter(name: string): string {
        return this.backService.topRouter(name);
    }

    menuURL(id: number, name: string): string {
        if (this.prefix.length === 0 || this.prefix === '/back') {
            return this.menuRouter(name);
        } else {
            if (this.menuRouter(name).length === 0) {
                return this.prefix + '/menu/' + id;
            }
        }
    }

}
