import {Component} from '@angular/core';
import {SupportService} from './support.service';

@Component({
    templateUrl: 'questions.component.html'
})

export class QuestionsComponent {
    constructor(private supportService: SupportService) {}

    deleteQuestion(id: number) {
        this.supportService.deleteQuestion(id);
    }

}
