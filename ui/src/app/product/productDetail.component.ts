import {Component, OnChanges, OnInit} from '@angular/core';
import {ProductService} from './product.service';
import {Product} from '../model/product';
import {Observable} from 'rxjs/Observable';
import {FileUploader} from 'ng2-file-upload';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {LoggerService} from '../utils/logger.service';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
    templateUrl: 'productDetail.component.html'
})

export class ProductDetailComponent implements OnInit, OnChanges {
    product$: Observable<Product>;
    selectedProduct: string;
    uploader: FileUploader = new FileUploader({
        url: '/api/product/addCategoryBanner',
        method: 'POST'
    });
    productForm: FormGroup;
    imageNames = ['', '', '', ''];
    // imageUrl: any;
    constructor(private productService: ProductService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private fb: FormBuilder) {
        this.createForm();
        this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
            this.logger.debug('ImageUpload:uploaded:' + item + status + response);
            if (response) {
                let json = JSON.parse(response);
                this.productForm.get('images').get('image' + json['index']).setValue(json['img']);
            }
        };
    }

    ngOnInit(): void {
        this.product$ = this.route.paramMap
            .switchMap((params: ParamMap) =>
                this.productService.getProduct(params.get('id')));
        this.route.paramMap.subscribe(params => {
                if (params.has('id')) {
                    this.selectedProduct = params.get('id');
                }
            }
        );
        this.product$.subscribe(p => {
            this.productForm.patchValue({
                name: p.name,
                category: p.category,
                sku: p.sku,
                content: p.content,
                asin: p.asin,
                price: p.price,
                sellPrice: p.sellPrice,
                stock: p.stock,
                show: p.show,
                attributes: p.attributes as Object[],
                images: {
                    image0: p.images['image0'] || '',
                    image1: p.images['image1'] || '',
                    image2: p.images['image2'] || '',
                    image3: p.images['image3'] || ''
                },
                link: p.link
            });
            this.setAttributes(p.attributes);
        });
    }

    createForm() {
        this.productForm = this.fb.group({
            name: '',
            category: 0,
            sku: '',
            content: '',
            asin: '',
            price: 0,
            sellPrice: 0,
            stock: 0,
            show: true,
            attributes: this.fb.array([]),
            images: this.fb.group({
               image0: '',
               image1: '',
               image2: '',
               image3: ''
            }),
            link: ''
        })
    }

    onSubmit() {
        let pid = this.selectedProduct == null ? 0 : +this.selectedProduct;
        let product = new Product(
            pid,
            this.productForm.get('name').value,
            this.productForm.get('sku').value,
            +this.productForm.get('category').value,
            0,
            this.attributes.getRawValue(),
            this.productForm.get('content').value,
            +this.productForm.get('price').value,
            +this.productForm.get('sellPrice').value,
            this.productForm.get('asin').value,
            +this.productForm.get('stock').value,
            this.productForm.get('show').value,
            this.productForm.get('images').value,
            this.productForm.get('link').value
        );
        this.productService.addProduct(product);
    }

    revert() {
        this.ngOnChanges();
    }

    ngOnChanges() {
        // let product = this.product$.pipe(async) as Product;
        this.product$.subscribe(p => {
                this.productForm.reset({
                    name: p.name
                });
            this.setAttributes(p.attributes);
        });
    }

    get attributes(): FormArray {
        return this.productForm.get('attributes') as FormArray;
    };

    setAttributes(attributes: Object[]) {
        const attributeFGs = attributes.map(attribute => this.fb.group(attribute));
        const attributeFormArray = this.fb.array(attributeFGs);
        this.productForm.setControl('attributes', attributeFormArray);
    }

    addAttribute() {
        this.attributes.push(this.fb.group({'key': '', 'value': ''}));
    }

    uploadImage(bt, spot) {
        // alert('name: '  + ', spot:' + spot + ', enter uploadImage');
        bt.click();
    }

    removeImage(spot) {
        // alert('enter removeImage' + spot);
        let name = 'image' + spot;
        this.productForm.get('images').get(name).setValue('');
    }

    onChangeSelectFile(event, i) {
        const file = event.currentTarget.files[0];
        if (file) {
            // 必须 bypassSecurityTrustUrl 转换一下 url ，要不能angular会报，说url不安全错误。
            // this.imageUrl = this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(file));
            this.productService.uploadProductPic(this.uploader, i, this.selectedProduct);
        }
    }

}
