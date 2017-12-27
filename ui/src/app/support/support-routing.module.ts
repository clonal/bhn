
import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {SupportNavComponent} from './supportNav.component';
import {QuestionsComponent} from './questions.component';
import {QuestionDetailComponent} from './questionDetail.component';
import {FeedBackComponent} from './feedback.component';

const supportRoutes: Routes = [
    {
        path: 'support',
        component: SupportNavComponent,
        children: [
            {
                path: 'questions',
                component: QuestionsComponent
            },
            {
                path: 'question/:id',
                component: QuestionDetailComponent
            },
            {
                path: 'question',
                component: QuestionDetailComponent
            },
            {
                path: 'feedback',
                component: FeedBackComponent
            },
            {
                path: '',
                component: QuestionsComponent
            }
        ]
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(supportRoutes)
    ],
    exports: [
        RouterModule
    ]
})

export class SupportRoutingModule {}
