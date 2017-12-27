import {NgModule} from '@angular/core';
import {AboutUsRoutingModule} from './aboutUs-routing.module';
import {AboutUsService} from './aboutUs.service';
import {AboutUsComponent} from './aboutUs.component';
import {AboutUsMenusComponent} from './aboutUsMenus.component';
import {AboutUsMenuDetailComponent} from './aboutUsMenuDetail.component';
import {AboutUsArticleDetailComponent} from './aboutUsArticleDetail.component';
import {AboutUsArticlesComponent} from './aboutUsArticles.component';
import {AboutUsNavComponent} from './aboutUsNav.component';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {FileUploadModule} from 'ng2-file-upload';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        FileUploadModule,
        AboutUsRoutingModule
    ],
    declarations: [
        AboutUsComponent,
        AboutUsMenusComponent,
        AboutUsMenuDetailComponent,
        AboutUsArticlesComponent,
        AboutUsArticleDetailComponent,
        AboutUsNavComponent,
    ],
    providers: [
        AboutUsService
    ]
})

export class AboutUsModule {}
