import {Component, OnInit} from '@angular/core';
import {ProductService} from './product.service';
import {Observable} from 'rxjs/Observable';
import {Category} from '../model/category';
import {LoggerService} from '../utils/logger.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {DateService} from '../utils/date.service';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: 'categoryDetail.component.html'
})

export class CategoryDetailComponent implements OnInit {
    category$: Observable<Category>;
    selectedCategory: string;
    uploader: FileUploader = new FileUploader({
        url: '/api/item/addCategoryBanner',
        method: 'POST'
    });
    constructor(private productService: ProductService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router,
                private dateService: DateService) {}


    ngOnInit(): void {
        this.category$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.productService.getCategory(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedCategory = params.get('id');
                }
            }
        );
    }

    submit(category: Category) {
        this.productService.addCategory(category, this.uploader);
    }

    edit(category: Category) {
        this.productService.editCategory(category, this.uploader);
    }

    chosenChanged(value: number, category: Category) {
        category.parent = +value;
    }
}
