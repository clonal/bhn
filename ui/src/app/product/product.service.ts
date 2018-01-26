import {Injectable} from '@angular/core';
import {BackendService} from '../backend/backend.service';
import {LoggerService} from '../utils/logger.service';
import {Observable} from 'rxjs/Observable';
import {Category} from '../model/category';
import {HttpClient} from '@angular/common/http';
import {FileUploader} from 'ng2-file-upload';
import {Product} from '../model/product';
import {Department} from '../model/department';

@Injectable()
export class ProductService {
    products: Product[];
    categories: Category[];
    departments: Department[];

    constructor(private backendService: BackendService,
                private logger: LoggerService,
                private client: HttpClient) {
        this.initProducts();
        this.initCategories();
        this.initDepartments();
    }

    initProducts() {
        this.backendService.getProducts().subscribe(result =>
            this.products = result
        );
    }

    initCategories() {
        this.client.get('/api/product/listCategories').subscribe(result =>
            this.categories = result as Category[]
        );
    }

    initDepartments() {
        this.client.get('/api/product/listDepartments').subscribe(result =>
            this.departments = result as Department[]
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

    getDepartment(id: string | any): Observable<Department> {
        let str = id == null ? '' : '?department=' + id;
        return this.client.get('/api/product/findDepartment' + str)
            .map(result => {
                if (result) {
                    if (result['error'] || result['info']) {
                        return new Department(0, '', '');
                    } else if (result['department']) {
                        return result['department'] as Department;
                    }
                } else {
                    return new Department(0, '', '');
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
            op.url = '/api/product/addCategoryBanner?category=' + c.id;
            uploader.setOptions(op);
            uploader.uploadAll();
            this.initCategories();
        });
    }

    addDepartment(department: Department) {
        this.client.post('/api/product/addDepartment', department).subscribe( result => {
            this.initDepartments();
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
                op.url = '/api/product/addCategoryBanner?category=' + category.id;
                uploader.setOptions(op);
                uploader.uploadAll();
                this.initCategories();
                alert(result['info']);
            }
        });
    }

    deleteProduct(id: number) {
        this.client.post('/api/product/removeProduct/' + id, {})
            .subscribe(result => {
               if (result['error']) {
                   alert(result['error']);
               }
               if (result['info']) {
                   this.initProducts();
                   alert(result['info']);
               }
            });
    }

    getProduct(id: string | any): Observable<Product> {
        let str = id == null ? '' : '?product=' + id;
        return this.client.get('/api/product/findProduct' + str)
            .map(result => {
                if (result) {
                    if (result['error'] || result['info']) {
                        return new Product(0, '', '', 0, 0, [], '',
                            0, 0, '', 0, false, {}, '');
                    } else if (result['product']) {
                        return result['product'] as Product;
                    }
                } else {
                    return new Product(0, '', '', 0, 0, [], '',
                        0, 0, '', 0, false, {}, '');
                }
            });
    }

    addProduct(product: Product) {
        this.client.post('/api/product/addProduct', product).subscribe(p => {
           this.initProducts();
        });
    }

    uploadProductPic(uploader: FileUploader, i: number, id: string) {
        let op = uploader.options;
        let idStr = id == null ? '' : '&pid=' + id;
        op.url = '/api/product/updateProductPic?index=' + i + idStr;
        uploader.setOptions(op);
        uploader.uploadAll();
    }
}
