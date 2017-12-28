import {Component} from '@angular/core';
import {ProductService} from './product.service';

@Component({
    templateUrl: 'itemList.component.html'
})

export class ItemListComponent {
    constructor(private productService: ProductService) {}

    deleteItem(id: number) {
        this.productService.deleteItem(id);
    }
}
