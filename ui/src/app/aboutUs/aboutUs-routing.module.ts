import {RouterModule, Routes} from '@angular/router';
import {AboutUsMenusComponent} from './aboutUsMenus.component';
import {AboutUsMenuDetailComponent} from './aboutUsMenuDetail.component';
import {AboutUsArticlesComponent} from './aboutUsArticles.component';
import {AboutUsArticleDetailComponent} from './aboutUsArticleDetail.component';
import {NgModule} from '@angular/core';
import {AboutUsNavComponent} from './aboutUsNav.component';

const aboutUsRoutes: Routes = [
    {
        path: 'aboutUs',
        // component: AboutUsComponent,
        component: AboutUsNavComponent,
        children: [
            // {
                // path: '',
                // component: AboutUsNavComponent,
                // children: [
                    {
                      path: 'menus',
                      component: AboutUsMenusComponent
                    },
                    {
                      path: 'menu/:id',
                      component: AboutUsMenuDetailComponent
                    },
                    {
                        path: 'addMenu',
                        component: AboutUsMenuDetailComponent
                    },
                    {
                        path: 'articles',
                        component: AboutUsArticlesComponent
                    },
                    {
                        path: 'article/:id',
                        component: AboutUsArticleDetailComponent
                    },
                    {
                        path: 'addArticle',
                        component: AboutUsArticleDetailComponent
                    },
                    {
                        path: '',
                        component: AboutUsMenusComponent
                    }
                // ]
            // }
        ]
    }
];


@NgModule({
    imports: [
        RouterModule.forChild(aboutUsRoutes)
    ],
    exports: [
        RouterModule
    ]
})

export class AboutUsRoutingModule {}
