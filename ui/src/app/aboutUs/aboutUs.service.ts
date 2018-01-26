import {Injectable} from '@angular/core';
import {Menu} from '../model/menu';
import {BackendService} from '../backend/backend.service';
import {FileUploader} from 'ng2-file-upload';
import {LoggerService} from '../utils/logger.service';
import {Observable} from 'rxjs/Observable';
import {Article} from '../model/article';
export const AboutUsMenu = 2;
@Injectable()
export class AboutUsService {

    menus: Menu[];
    articles: Article[];
    constructor(private backendService: BackendService, private logger: LoggerService) {
        this.initMenus();
        this.initArticles();
    }

    initMenus() {
        this.backendService.getChildMenus(AboutUsMenu).subscribe(result =>
            this.menus = result
        );
    }

    initArticles() {
        this.backendService.getArticles(AboutUsMenu.toString())
            .subscribe(result => this.articles = result);
    }

    deleteMenu(id: number) {
        this.backendService.deleteMenu(id).subscribe(flag => {
                if (flag) {
/*                    let index = this.menus.findIndex(m => m.id === id);
                    this.menus.splice(index, 1);*/
                    this.initMenus();
                }
            }
         );
    }

    addChild(menus: Menu[], parent: number, child: Menu) {
        if (parent === AboutUsMenu) {
            menus.push(child);
        } else {
            menus.forEach(c => {
                if (c.id === parent) {
                    c.children.push(child);
                } else {
                    this.addChild(c.children, parent, child);
                }
            });
        }
    }

    addMenu(json: any, uploader: FileUploader) {
        return this.backendService.addMenu(json).subscribe(result => {
            let op = uploader.options;
            op.url = '/api/menu/addBannerImages?menu=' + result.id;
            uploader.setOptions(op);
            uploader.uploadAll();
            // this.addChild(this.menus, result.parent, result);
            this.initMenus();
        });
    }

    nextMenuOrder(): number {
        return this.menus[this.menus.length - 1].order + 1;
    }

    nextArticleOrder(menus: number): number {
        return this.articles[this.menus.length - 1].order + 1;
    }

    getMenu(id: string | any): Observable<Menu> {
        return this.backendService.getMenu(id);
    }

    editMenu(selectedMenu: string, json: any, uploader: FileUploader) {
        return this.backendService.editMenu(selectedMenu, json).subscribe(result => {
            if (result) {
                let op = uploader.options;
                op.url = '/api/menu/editImage/' + selectedMenu + '/1';
                uploader.setOptions(op);
                uploader.uploadAll();
                this.initMenus();
                this.logger.debug(' menus:' + JSON.stringify(this.menus));
            }
        });
    }

    getArticle(id: string | any): Observable<Article> {
        return this.backendService.getArticle(id);
    }

    addArticle(json: any): Observable<boolean> {
        return this.backendService.addArticle(json);
    }

    deleteArticle(id: number) {
        this.backendService.deleteArticle(id).subscribe(flag => {
                if (flag) {
                    this.initArticles();
                }
            }
        );
    }
}
