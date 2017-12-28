import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProductNavComponent} from './productNav.component';
import {ItemListComponent} from './itemList.component';
import {ItemDetailComponent} from './itemDetail.component';
import {CategoryListComponent} from './categoryList.component';
import {CategoryDetailComponent} from './categoryDetail.component';

const productRoutes: Routes = [
    {
        path: 'item',
        component: ProductNavComponent,
        children: [
            {
                path: 'items',
                component: ItemListComponent
            },
            {
                path: 'item/:id',
                component: ItemDetailComponent
            },
            {
                path: 'addItem',
                component: ItemDetailComponent
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
                component: ItemListComponent
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
