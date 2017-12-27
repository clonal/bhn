import {Component} from '@angular/core';
import {SupportService} from './support.service';

@Component({
    templateUrl: 'feedback.component.html'
})

export class FeedBackComponent {
    constructor(private supportService: SupportService) {}

    deleteFeedback(id: number) {
        this.supportService.deleteFeedback(id);
    }
}
