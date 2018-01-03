import {NgModule} from '@angular/core';
import {ProductRoutingModule} from './product-routing.module';
import {FileUploadModule} from 'ng2-file-upload';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ProductService} from './product.service';
import {ProductComponent} from './product.component';
import {CategoryDetailComponent} from './categoryDetail.component';
import {CategoryListComponent} from './categoryList.component';
import {ProductNavComponent} from './productNav.component';
import {CategorySelectorComponent} from './categorySelector.component';
import {ProductDetailComponent} from './productDetail.component';
import {ProductListComponent} from './productList.component';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        FileUploadModule,
        ProductRoutingModule,
    ],
    declarations: [
        ProductComponent,
        CategoryDetailComponent,
        CategoryListComponent,
        ProductDetailComponent,
        ProductListComponent,
        ProductNavComponent,
        CategorySelectorComponent,
    ],
    providers: [
        ProductService,
    ]
})

export class ProductModule {}
