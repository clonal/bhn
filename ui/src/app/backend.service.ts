import {Injectable} from '@angular/core';
import {Menu} from './model/menu';
import {LoggerService} from './utils/logger.service';
import {Http} from '@angular/http';
import {Banner} from './model/banner';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {Article} from './model/article';
import {Item} from './model/item';

@Injectable()
export class BackendService {

    menus: Menu[];
    constructor(private logger: LoggerService, private http: Http, private client: HttpClient) {}

    initMenus(): void {
        this.getMenus().then(result => {
                this.menus = result; this.logger.debug('menus: ' + this.menus.length);
            }
        );
    }

    getBanners(): Promise<Banner[]> {
        return this.http.get('/api/menu/showMenu/1')
            .toPromise()
            .then( response => {
                    let banner = response.json().banner as Map<string, string>;
                    let banners = [];
                    for (let key in banner) {
                        if (key) {
                            banners.push(new Banner(Number(key), banner[key]));
                        }
                    }
                    return banners;
                }
            );
    }

    getMenus(): Promise<Menu[]> {
        return this.http.get('/api/menu/listMenus')
            .toPromise()
            .then(response =>
                response.json() as Menu[]
            );
    }

    getChildMenus(root: number): Promise<Menu[]> {
        return this.http.get('/api/menu/findChildrenOfMenu/' + root)
            .toPromise()
            .then(response =>
                response.json() as Menu[]
            );
    }

    deleteRootBanner(order: number, selectedMenu: number): Observable<boolean> {
        return this.client.post('/api/menu/deleteImage', {'index': String(order), 'menu': selectedMenu}).map((result) => {
                return true;
        });
    }

    editRootBanner(order: number, selectedMenu: number): Observable<boolean> {
        return this.client.post('/api/menu/editImage/' + selectedMenu + '/' + order, {}).map((result) => {
            return true;
        });
    }

    topRouter(key: string): string {
        let src = '';
        switch (key) {
            case 'root':
                src = '/back';
                break;
            case 'aboutUs':
                src = '/aboutUs';
                break;
            case 'support':
                src = '/support';
                break;
            case 'productManager':
                src = '/item';
                break;
        }
        return src;
    }

    deleteMenu(id: number): Observable<boolean> {
        return this.client.post('/api/menu/removeMenu/' + id, {}).map( (data) => {
            let flag = true;
                if (data['error']) {
                    alert(data['error']);
                    flag = false;
                }
                if (data['info']) {
                    // this.logger.debug(' menus:' + JSON.stringify(this.menus));
                    // this.removeChild(this.menus[0], id);
                    this.initMenus();
                    flag = true;
                }
                return flag;
            }
        );
    }

    removeChild(menu: Menu, id: number): number {
        if (menu.id === id) {
            return 0;
        } else {
            let children = menu.children;
            let index = children.findIndex(b => b.id === id);
            if (index >= 0) {
                children.splice(index, 1);
                return index;
            }
            if (index < 0 && children.length > 0) {
                let i = -1;
                children.forEach(child => {
                    i = this.removeChild(child, id);
                    if (i >= 0) {
                        return i;
                    }
                });
                return i;
            }
            return -1;
        }
    }

    addChild(menu: Menu, parent: number, child: Menu) {
        if (menu.id === parent) {
            menu.children.push(child);
            return;
        } else {
            let children = menu.children;
            children.forEach(c => {
                this.addChild(c, parent, child);
            });
        }
    }

    addMenu(json: any): Observable<Menu> {
        return this.client.post('/api/menu/addMenu', json).map((result) => {
                let id = result['id'];
                if (id) {
                    let menu = new Menu(id, result['name'], result['order'], result['parent'],
                        result['content'], result['desc'], {}, []);
                    // this.addChild(this.menus[0], result['parent'], menu);
                    this.initMenus();
                    return menu;
                } else {
                    return null;
                }
            }
        );
    }

    getMenu(id: string | any): Observable<Menu> {
        let str = id == null ? '' : id;
        return this.client.get('/api/menu/showMenu/' + str).map((result) => {
            if (result) {
                if (result['error']) {
                    return new Menu(0, '', 0, 0, '', '', {}, []);
                } else if (result['menu']) {
                    return result['menu'] as Menu;
                }
            } else {
                return new Menu(0, '', 0, 0, '', '', {}, []);
            }
        });
    }

    editMenu(selectedMenu: string, json: any): Observable<boolean> {
        return this.client.post('/api/menu/editMenu/' + selectedMenu, json).map((data) => {
            let flag = false;
            if (data['error']) {
                alert(data['error']);
                flag = false;
            }
            if (data['info']) {
                flag = true;
                this.initMenus();
            }
            return flag;
        });
    }

    getArticles(menu: string): Observable<Article[]> {
        return this.client.get('/api/article/showArticle/listArticles/' + menu + '?recursive=true').map((result) => {
                return result as Article[];
            }
        );
    }

    getArticle(id: string | any): Observable<Article> {
        let str = id == null ? '' : id;
        return this.client.get('/api/article/showArticle/' + str)
            .map((result) => {
                if (result) {
                    if (result['error']) {
                        return new Article(0, 0, '', '', '', 0, '');
                    } else if (result['article']) {
                        return result['article'] as Article;
                    }
                } else {
                    return new Article(0, 0, '', '', '', 0, '');
                }
            });
    }

    addArticle(json: any): Observable<boolean> {
        return this.client.post('/api/article/addArticle', json)
            .map((result) => {
                if (result) {
                    if (result['error']) {
                        return false;
                    } else if (result['info']) {
                        return true;
                    }
                }
            });
    }

    deleteArticle(id: number): Observable<boolean> {
        return this.client.get('/api/article/removeArticle/' + id)
            .map((data) => {
                let flag = true;
                if (data['error']) {
                    alert(data['error']);
                    flag = false;
                }
                return flag;
            });
    }

    getItems(): Observable<Item[]> {
        return this.client.get('/api/item/listItems')
            .map((data) => {
                return data as Item[];
            });
    }
}
