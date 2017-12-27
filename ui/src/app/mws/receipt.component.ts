import { Component } from '@angular/core';

import {LoggerService} from '../utils/logger.service';
import {MwsService} from './mws.service';

@Component({
    templateUrl: 'receipt.component.html'
})

export class ReceiptComponent {
    from: String = '';
    to: String = '';

    filterTemplate: number = 0;
    sendTemplate: number = 1;
    isSend: boolean;
    duration: number = 0;

    constructor(private logger: LoggerService, private mwsService: MwsService) {}

    doWork(): void {
        if (this.from === '' && this.to === '') {
            alert('起始时间和截至时间必须选择一个');
            return;
        }
        // var urlParam = new URLSearchParams();
        let param = new Map();
        let fromDate = this.from + ':00Z';
        let toDate = this.to + ':00Z';
        if (this.from !== '') {
           param.set('from', fromDate);
           // urlParam.set('from', fromDate);
        }
        if (this.to !== '') {
           param.set('to', toDate);
           // urlParam.set('to', toDate);
        }
        param.set('template', this.sendTemplate);
        // urlParam.set('template', this.sendTemplate.toString());



        let paramStr = '';
        param.forEach(function(value, key, map){
            paramStr += '&' + key + '=' + encodeURI(value);
        });
        paramStr = '?' + paramStr.slice(1);
        this.logger.debug('from:' + fromDate + ',to:' + toDate + ',isSend:' + this.isSend + ',duration:' + this.duration);
        this.logger.debug('filterTemplate:' + this.filterTemplate + ',sendTemplate:' + this.sendTemplate);

        this.mwsService.sendReceipt(paramStr).toPromise().then((ok) => {
            this.logger.debug('后台开始处理，请耐心等待');
        }).catch((error) => {
            console.log(error);
        });
    }
}

