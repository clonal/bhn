import { LoggerService } from '../utils/logger.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Injectable} from '@angular/core';

@Injectable()
export class MwsService {

    constructor(private logger: LoggerService,  private http: HttpClient) {}

    sendReceipt(param: String): Observable<boolean> {
        let url = '/api/mws/sendmails' + param;
        this.logger.debug('进入post,' + url);
        return this.http.post(url, {})
            .map((result) => {
                return true;
            });
    }
}
