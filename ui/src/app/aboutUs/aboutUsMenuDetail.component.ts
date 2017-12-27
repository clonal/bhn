import {Component, OnInit} from '@angular/core';
import {LoggerService} from '../utils/logger.service';
import {AboutUsMenu, AboutUsService} from './aboutUs.service';
import {FileUploader} from 'ng2-file-upload';
import {Observable} from 'rxjs/Observable';
import {Menu} from '../model/menu';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';


@Component({
    templateUrl: 'aboutUsMenuDetail.component.html'
})

export class AboutUsMenuDetailComponent implements OnInit {
    menu$: Observable<Menu>;
    aboutUsMenu = AboutUsMenu;
    selectedMenu: string;
    // parent: number = this.aboutUsMenu;
    // menuName: string = '';
    // desc: string = '';
    // content: string = '';
    uploader: FileUploader = new FileUploader({
        url: '/api/menu/addBannerImages',
        method: 'POST'
    });

    ngOnInit(): void {
        this.menu$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.aboutService.getMenu(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedMenu = params.get('id');
                }
            }
        );
    }

    constructor(private aboutService: AboutUsService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router) {}

    submit(menu: Menu) {
        // let json1 = JSON.stringify(menu);
        let json = {'name': menu.name, 'parent': menu.parent, 'banner': {},
            'desc': menu.desc, 'content': menu.content, 'order': this.aboutService.nextMenuOrder(),
            'required': false};
        json.parent = +json.parent;
        this.aboutService.addMenu(json, this.uploader);
    }

    edit(menu: Menu) {
        let json = {'name': menu.name, 'parent': menu.parent,
            'desc': menu.desc, 'content': menu.content};
        this.aboutService.editMenu(this.selectedMenu, json, this.uploader);
    }

}
