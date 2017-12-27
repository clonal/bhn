import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Question} from '../model/question';
import {SupportService} from './support.service';
import {LoggerService} from '../utils/logger.service';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';

@Component({
    templateUrl: 'questionDetail.component.html'
})

export class QuestionDetailComponent implements OnInit {
    question$: Observable<Question>;
    selectedQuestion: string;

    constructor(private supportService: SupportService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router) {}

    ngOnInit(): void {
        this.question$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.supportService.getQuestion(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedQuestion = params.get('id');
                }
            }
        );
    }

    edit(question: Question) {
        this.supportService.editQuestion(question);
    }

    submit(question: Question) {
        this.supportService.addQuestion(question);
    }
}
