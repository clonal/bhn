import { Component } from '@angular/core';
import { Router } from '@angular/router';

// import {FileUploadModule, Message, GrowlModule} from 'primeng/primeng';
import {LoggerService} from "../utils/logger.service";

import {FileUploader} from 'ng2-file-upload';

@Component({
  templateUrl: 'backHome.component.html'
})
export class BackHomeComponent {

    // msgs: Message[];

    // uploadedFiles: any[] = [];

    uploader: FileUploader = new FileUploader({
        url: "http://localhost/api/menu/editImage/1/1",
        method: "POST",
        itemAlias: "uploadedfile"
    });

    constructor(private logger: LoggerService) {}

    ss(evt: ProgressEvent) {
        this.logger.debug('percent: ' + 100.0 * evt.loaded / evt.total);
    }

    selectedFileOnChanged() {
        // 这里是文件选择完成后的操作处理

        this.uploader.queue[0].onSuccess = (response, status, headers) => {
            // 上传文件成功
            if (status === 200) {
                // 上传文件后获取服务器返回的数据
                let tempRes = JSON.parse(response);
                this.logger.debug("upload completed:" + tempRes);
            }else {
                // 上传文件后获取服务器返回的数据错误
                this.logger.debug("upload error");
            }
        };
        this.uploader.queue[0].upload(); // 开始上传
    }



    // onUpload(event: any) {
    //     for(let file of event.files) {
    //         this.uploadedFiles.push(file);
    //     }
    //     this.msgs = [];
    //     this.msgs.push({severity: 'info', summary: 'File Uploaded', detail: ''});
    // }
    //
    // onUploadHandler(event: any) {
    //     this.logger.debug("12345");
    //     for(let file of event.files) {
    //         this.logger.debug("1111" + file.name);
    //         this.uploadedFiles.push(file);
    //     }
    // }
}
