import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import { FileUploader } from 'ng2-file-upload';

const URL = '/api/menu/addBannerImages';

@Component({
    selector: 'upload-image',
    templateUrl: './upload-image.component.html'
})
export class UploadImageComponent implements OnChanges {
    @Input() menu: number;
    public uploader: FileUploader = new FileUploader({url: URL});

    ngOnChanges(changes: SimpleChanges): void {
        this.uploader = new FileUploader({url: URL + '?menu=' + this.menu});
    }
}
