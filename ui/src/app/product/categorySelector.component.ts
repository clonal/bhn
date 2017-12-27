import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ProductService} from './product.service';
import {Category} from '../model/category';

@Component({
    selector: 'category-selector',
    templateUrl: 'categorySelector.component.html'
})

export class CategorySelectorComponent implements OnInit {
    @Input() chosen: number;
    @Output() chosenChange = new EventEmitter<number>();
    topCategories: Category[];
    constructor(private productService: ProductService) {}

    ngOnInit(): void {
        this.initTopCategories();
    }

    initTopCategories() {
        this.productService.getTopCategories().subscribe(result =>
            this.topCategories = result
        );
    }

    change(value: number) {
        this.chosenChange.emit(value);
    }
}
