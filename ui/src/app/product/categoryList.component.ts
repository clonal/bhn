import {Component} from '@angular/core';
import {ProductService} from './product.service';
import {NavigationExtras, Router} from '@angular/router';

@Component({
    templateUrl: 'categoryList.component.html'
})

export class CategoryListComponent {
    constructor(private productService: ProductService,
                private router: Router
                ) {}

    deleteCategory(id: number) {
        this.productService.deleteCategory(id);
    }
/*
    jump(id: number) {
        let navigationExtras: NavigationExtras = {
            queryParams: { 'session_id': sessionId }
        };

        // Navigate to the login page with extras
        this.router.navigate(['/login'], navigationExtras);
    }*/
}
