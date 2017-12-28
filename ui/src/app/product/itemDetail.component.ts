import {Component, OnInit} from '@angular/core';
import {Item} from '../model/item';
import {FileUploader} from 'ng2-file-upload';
import {ProductService} from './product.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {LoggerService} from '../utils/logger.service';
import {Observable} from 'rxjs/Observable';

@Component({
    templateUrl: 'itemDetail.component.html'
})

export class ItemDetailComponent implements OnInit {
    item$: Observable<Item>;
    selectedItem: string;
    uploader: FileUploader = new FileUploader({
        url: '/api/item/addCategoryBanner',
        method: 'POST'
    });
    constructor(private productService: ProductService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router) {}


    ngOnInit(): void {
        this.item$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.productService.getItem(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedItem = params.get('id');
                }
            }
        );
    }
}

