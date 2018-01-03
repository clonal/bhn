import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProductNavComponent} from './productNav.component';
import {CategoryListComponent} from './categoryList.component';
import {CategoryDetailComponent} from './categoryDetail.component';
import {ProductListComponent} from './productList.component';
import {ProductDetailComponent} from './productDetail.component';


const productRoutes: Routes = [
    {
        path: 'product',
        component: ProductNavComponent,
        children: [
            {
                path: 'products',
                component: ProductListComponent
            },
            {
                path: 'product/:id',
                component: ProductDetailComponent
            },
            {
                path: 'addItem',
                component: ProductDetailComponent
            },
            {
                path: 'categories',
                component: CategoryListComponent
            },
            {
                path: 'category/:id',
                component: CategoryDetailComponent
            },
            {
                path: 'addCategory',
                component: CategoryDetailComponent
            },
            {
                path: '',
                component: ProductListComponent
            }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(productRoutes)
    ],
    exports: [
        RouterModule
    ]
})

export class ProductRoutingModule {}
