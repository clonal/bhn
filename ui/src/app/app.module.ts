import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import { FormsModule }   from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';

import { JwtModule, JWT_OPTIONS } from '@auth0/angular-jwt';

import { AppComponent }  from './app.component';
import { LoginComponent }  from './login/login.component';
import { PageNotFoundComponent } from './utils/page-not-found.component';
import { AppRoutingModule } from './app-routing.module';

import { LoggerService } from './utils/logger.service';
import { TOKEN_NAME, AuthService } from './auth/auth.service';
import { LoginService } from './login/login.service';
import { SecuredGuard } from './auth/secured-guard.service';
import {SignUpComponent} from './login/signUp.component';
import {ActivateAccountComponent} from './login/activateAccount.component';
import {FileUploadModule} from 'ng2-file-upload';
import {CommonModule} from '@angular/common';
import {ReceiptComponent} from './mws/receipt.component';
import {MwsService} from './mws/mws.service';
import {BackendComponent} from './backend.component';
import {BackendService} from './backend.service';
import {HttpModule} from '@angular/http';
import {TreeMenuComponent} from './tree-menu.component';
import {UploadImageComponent} from './upload-image.component';
import {AboutUsModule} from './aboutUs/aboutUs.module';
import {DateService} from './utils/date.service';
import {SupportModule} from './support/support.module';
import {ProductModule} from './product/product.module';


export function jwtOptionsFactory(): any {
    return {
        tokenGetter: () => {
            return localStorage.getItem(TOKEN_NAME);
        }
    };
}

@NgModule({
    imports: [
        CommonModule,
        FileUploadModule,
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        HttpClientModule,
        JwtModule.forRoot({
            jwtOptionsProvider: {
                provide: JWT_OPTIONS,
                useFactory: jwtOptionsFactory
            },
            config: { skipWhenExpired: true }
        }),
        AboutUsModule,
        SupportModule,
        ProductModule,
        AppRoutingModule
    ],
    providers: [
        AuthService,
        LoggerService,
        LoginService,
        MwsService,
        SecuredGuard,
        BackendService,
        DateService,
    ],
    declarations: [
        AppComponent,
        LoginComponent,
        PageNotFoundComponent,
        SignUpComponent,
        ActivateAccountComponent,
        ReceiptComponent,
        BackendComponent,
        TreeMenuComponent,
        UploadImageComponent,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    bootstrap:    [ AppComponent ]
})
export class AppModule { }
