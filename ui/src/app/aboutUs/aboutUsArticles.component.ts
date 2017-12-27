import {Component} from '@angular/core';
import {AboutUsService} from './aboutUs.service';

@Component({
    templateUrl: 'aboutUsArticles.component.html'
})

export class AboutUsArticlesComponent {
    constructor(private aboutService: AboutUsService) {}

    deleteArticle(id: number) {
        this.aboutService.deleteArticle(id);
    }
}
