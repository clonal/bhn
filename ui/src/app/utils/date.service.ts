
import {Injectable} from '@angular/core';

@Injectable()
export class DateService {
    fmtDate(): string {
        let date = new Date();
        let y = date.getFullYear();
        let m = date.getMonth() + 1;
        let om = m < 10 ? ('0' + m) : m;
        let d = date.getDate();
        let od = d < 10 ? ('0' + d) : d;
        let h = date.getHours();
        let oh = h < 10 ? ('0' + h) : h;
        let minute = date.getMinutes();
        let second = date.getSeconds();
        let oMinute = minute < 10 ? ('0' + minute) : minute;
        let oSecond = second < 10 ? ('0' + second) : second;
        return y + '-' + om + '-' + od + ' ' + oh + ':' + oMinute + ':' + oSecond;
    }
}
