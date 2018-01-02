import {Injectable} from '@angular/core';
import {BackendService} from '../backend.service';
import {LoggerService} from '../utils/logger.service';
import {Observable} from 'rxjs/Observable';
import {Category} from '../model/category';
import {HttpClient} from '@angular/common/http';
import {FileUploader} from 'ng2-file-upload';
import {Item} from '../model/item';

@Injectable()
export class ProductService {
    items: Item[];
    categories: Category[];

    constructor(private backendService: BackendService,
                private logger: LoggerService,
                private client: HttpClient) {
        this.initItems();
        this.initCategories();
    }

    initItems() {
        this.backendService.getItems().subscribe(result =>
            this.items = result
        );
    }

    initCategories() {
        this.client.get('/api/product/listCategories').subscribe(result =>
            this.categories = result as Category[]
        );
    }

    getCategory(id: string | any): Observable<Category> {
        let str = id == null ? '' : '?category=' + id;
        return this.client.get('/api/product/findCategory' + str)
            .map(result => {
                if (result) {
                    if (result['error'] || result['info']) {
                        return new Category(0, '', '', 0, '');
                    } else if (result['category']) {
                        return result['category'] as Category;
                    }
                } else {
                    return new Category(0, '', '', 0, '');
                }
            });
    }

    getTopCategories(): Observable<Category[]> {
        return this.client.get('api/product/listTopCategories')
            .map(result => {
                return result as Category[];
            });
    }

    addCategory(category: Category, uploader: FileUploader) {
        this.client.post('/api/product/addCategory', category).subscribe( result => {
            let c = result as Category;
            let op = uploader.options;
            op.url = '/api/item/addCategoryBanner?category=' + c.id;
            uploader.setOptions(op);
            uploader.uploadAll();
            this.initCategories();
        });
    }

    deleteCategory(id: number) {
        this.client.post('/api/product/removeCategory/' + id, {}).subscribe(result => {
           if (result['info']) {
               this.initCategories();
               alert(result['info']);
           }
        });
    }

    editCategory(category: Category, uploader: FileUploader) {
        this.client.post('/api/product/editCategory', category).subscribe(result => {
            if (result['info']) {
                let op = uploader.options;
                op.url = '/api/item/addCategoryBanner?category=' + category.id;
                uploader.setOptions(op);
                uploader.uploadAll();
                this.initCategories();
                alert(result['info']);
            }
        });
    }

    deleteItem(id: number) {
        this.client.post('/api/product/removeItem/' + id, {})
            .subscribe(result => {
               if (result['error']) {
                   alert(result['error']);
               }
               if (result['info']) {
                   this.initItems();
                   alert(result['info']);
               }
            });
    }

    getItem(id: string | any): Observable<Item> {
        let str = id == null ? '' : '?item=' + id;
        return this.client.get('/api/product/findItem' + str)
            .map(result => {
                if (result) {
                    if (result['error'] || result['info']) {
                        return new Item(0, '', '', [0], [0], '');
                    } else if (result['item']) {
                        return result['item'] as Item;
                    }
                } else {
                    return new Item(0, '', '', [0], [0], '');
                }
            });
    }
}
