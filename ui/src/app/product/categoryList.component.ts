import {Component} from '@angular/core';
import {ProductService} from './product.service';

@Component({
    templateUrl: 'categoryList.component.html'
})

export class CategoryListComponent {
    constructor(private productService: ProductService) {}

    deleteCategory(id: number) {
        this.productService.deleteCategory(id);
    }
}
