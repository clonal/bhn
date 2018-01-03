import {Component} from '@angular/core';
import {ProductService} from './product.service';

@Component({
    templateUrl: 'productList.component.html'
})

export class ProductListComponent {
    constructor(private productService: ProductService) {}

    deleteProduct(id: number) {
        this.productService.deleteProduct(id)
    }
}
