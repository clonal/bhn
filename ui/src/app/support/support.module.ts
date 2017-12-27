
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SupportNavComponent} from './supportNav.component';
import {QuestionsComponent} from './questions.component';
import {QuestionDetailComponent} from './questionDetail.component';
import {FeedBackComponent} from './feedback.component';
import {SupportService} from './support.service';
import {SupportComponent} from './support.component';
import {SupportRoutingModule} from './support-routing.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        SupportRoutingModule,
    ],
    declarations: [
        SupportNavComponent,
        SupportComponent,
        QuestionsComponent,
        QuestionDetailComponent,
        FeedBackComponent,
    ],
    providers: [
        SupportService
    ]
 })

export class SupportModule {}
