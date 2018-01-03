import {Component, OnInit} from '@angular/core';
import {ProductService} from './product.service';
import {Product} from '../model/product';
import {Observable} from 'rxjs/Observable';
import {FileUploader} from 'ng2-file-upload';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {LoggerService} from '../utils/logger.service';

@Component({
    templateUrl: 'productDetail.component.html'
})

export class ProductDetailComponent implements OnInit {
    product$: Observable<Product>;
    selectedProduct: string;
    index = 1;
    attr: any = [{'name': '', 'value': ''}];
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

    addInput() {
        let number = this.attr.length + 1;
        this.attr.push({'name': '', 'value': ''});
    }

    removeInput(i) {
        let j = this.attr.indexOf(i);
        this.attr.splice(j, 1);
    }
}
