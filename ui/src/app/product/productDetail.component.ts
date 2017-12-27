import {Component, OnInit} from '@angular/core';
import {Product} from '../model/product';
import {FileUploader} from 'ng2-file-upload';
import {ProductService} from './product.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {LoggerService} from '../utils/logger.service';
import {Observable} from 'rxjs/Observable';

@Component({
    templateUrl: 'productDetail.component.html'
})

export class ProductDetailComponent implements OnInit{
    product$: Observable<Product>;
    selectedProduct: string;
    uploader: FileUploader = new FileUploader({
        url: '/api/product/addCategoryBanner',
        method: 'POST'
    });
    constructor(private productService: ProductService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router) {}


    ngOnInit(): void {
        this.product$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.productService.getProduct(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedProduct = params.get('id');
                }
            }
        );
    }
}

