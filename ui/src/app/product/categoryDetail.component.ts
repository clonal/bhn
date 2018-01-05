import {Component, OnChanges, OnInit} from '@angular/core';
import {ProductService} from './product.service';
import {Observable} from 'rxjs/Observable';
import {Category} from '../model/category';
import {LoggerService} from '../utils/logger.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DateService} from '../utils/date.service';
import {FileUploader} from 'ng2-file-upload';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Department} from '../model/department';

@Component({
    templateUrl: 'categoryDetail.component.html'
})

export class CategoryDetailComponent implements OnInit, OnChanges {
    category$: Observable<Category>;
    department$: Observable<Department>;
    selectedCategory: string;
    uploader: FileUploader = new FileUploader({
        url: '/api/product/addCategoryBanner',
        method: 'POST'
    });
    categoryForm: FormGroup;
    isDepartment = '';
    banner = '';
    constructor(private productService: ProductService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router,
                private dateService: DateService,
                private fb: FormBuilder) {
        this.createForm();
    }

    createForm() {
        this.categoryForm = this.fb.group({
            type: 'department',
            name: '',
            desc: '',
            department: 0
        })
    }

    onSubmit() {
        let type = this.categoryForm.get('type').value;
        switch (type) {
            case 'department':
                let department = new Department(
                    0,
                    this.categoryForm.get('name').value,
                    this.categoryForm.get('desc').value
                );
                this.productService.addDepartment(department);
                break;
            case 'category':
                let category = new Category(
                    0,
                    this.categoryForm.get('name').value,
                    this.categoryForm.get('desc').value,
                    +this.categoryForm.get('department').value,
                    this.banner
                );
                this.productService.addCategory(category, this.uploader);
                break;
            default:
                break;
        }
    }

    revert() {
        this.ngOnChanges();
    }

    ngOnInit(): void {
        this.route.queryParamMap.subscribe(params => {
                this.selectedCategory = params.has('id') ? params.get('id') : null;
                this.isDepartment = params.has('type') ? params.get('type') : '';
                switch (this.isDepartment) {
                    case 'category':
                        this.category$ = this.productService.getCategory(this.selectedCategory);
                        this.category$.subscribe(c => {
                            this.categoryForm.reset({
                                // type: this.isDepartment,
                                type: {value: this.isDepartment, disabled: true},
                                name: c.name,
                                desc: c.desc,
                                department: c.department
                            });
                            this.banner = c.banner;
                        });
                        break;
                    case 'department':
                        this.department$ = this.productService.getDepartment(this.selectedCategory);
                        this.department$.subscribe(c => {
                            this.categoryForm.reset({
                                // type: this.isDepartment,
                                type: {value: this.isDepartment, disabled: true},
                                name: c.name,
                                desc: c.desc
                            });
                        });
                        break;
                    default:
                        break;
                }
                if (this.isDepartment === '') {
                    this.isDepartment = 'department';
                }
            }
        );

    }

    ngOnChanges(): void {
        if (this.isDepartment === '' || this.isDepartment === 'department') {
            this.department$.subscribe(e => {
                this.categoryForm.reset({
                    // type: 'department',
                    type: {value: 'department', disabled: this.selectedCategory != null},
                    name: e.name,
                    desc: e.desc
                });
            });
        } else {
            this.category$.subscribe(e => {
                this.categoryForm.reset({
                    // type: 'category',
                    type: {value: 'category', disabled: this.selectedCategory != null},
                    name: e.name,
                    desc: e.desc,
                    department: e.department
                });
            });
        }
    }
}
