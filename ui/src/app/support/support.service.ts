import {Injectable} from '@angular/core';
import {Question} from '../model/question';
import {LoggerService} from '../utils/logger.service';
import {BackendService} from '../backend.service';
import {HttpClient} from '@angular/common/http';
import {Feedback} from '../model/feedback';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class SupportService {
    questions: Question[];
    feedbacks: Feedback[];

    constructor(private backService: BackendService,
                private client: HttpClient,
                private logger: LoggerService) {
        this.initQuestions();
        this.initFeedbacks();
    }

    initQuestions() {
        this.client.get('/api/question/listQuestions')
            .subscribe(result => {
                this.questions = result as Question[];
            });
    }

    initFeedbacks() {
        this.client.get('api/feedback/listFeedbacks')
            .subscribe(result => {
                this.feedbacks = result as Feedback[];
            });
    }

    deleteQuestion(id: number) {
        this.client.post('/api/question/deleteQuestion/' + id, {})
            .subscribe(result => {
                if (result['error']) {
                    this.logger.debug('delete question error');
                }
                if (result['info']) {
                    this.logger.debug('delete success');
                    this.initQuestions();
                }
            });
    }

    getQuestion(id: string | any): Observable<Question> {
        let str = id == null ? '' : id;
        return this.client.get('/api/question/getQuestion/' + str)
            .map(result => {
                if (result) {
                    if (result['error']) {
                        return new Question(0, '', '');
                    } else if (result['question']) {
                        return result['question'] as Question;
                    }
                } else {
                    return new Question(0, '', '');
                }
            });
    }

    addQuestion(question: Question) {
        this.client.post('/api/question/addQuestion', question)
            .subscribe(r => this.initQuestions());
    }

    editQuestion(question: Question) {
        this.client.post('/api/question/editQuestion', question)
            .subscribe(r => this.initQuestions());
    }

    deleteFeedback(id: number) {
        this.client.post('/api/feedback/deleteFeedback/' + id, {})
            .subscribe(result => {
                if (result['error']) {
                    this.logger.debug('delete feedback error');
                }
                if (result['info']) {
                    alert(result['info']);
                    this.logger.debug('delete feedback success');
                    this.initFeedbacks();
                }
            });
    }
}
