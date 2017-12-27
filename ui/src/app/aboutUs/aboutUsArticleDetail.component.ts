import {Component, OnInit} from '@angular/core';
import {AboutUsService} from './aboutUs.service';
import {Observable} from 'rxjs/Observable';
import {Article} from '../model/article';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {LoggerService} from '../utils/logger.service';
import {DateService} from '../utils/date.service';

@Component({
    templateUrl: 'aboutUsArticleDetail.component.html'
})

export class AboutUsArticleDetailComponent implements OnInit {
    article$: Observable<Article>;
    selectedArticle: string;
    constructor(private aboutService: AboutUsService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router,
                private dateService: DateService) {}

    ngOnInit(): void {
        this.article$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.aboutService.getArticle(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedArticle = params.get('id');
                }
            }
        );
    }

    submit(article: Article) {
        let json = {'menu': +article.menu, 'title': article.title, 'desc': article.desc,
            'content': article.content, 'date': this.dateService.fmtDate(), 'order': 0};
        this.aboutService.addArticle(json).subscribe(result => {
            if (result) {
                this.aboutService.initArticles();
            }
        });
    }
}
