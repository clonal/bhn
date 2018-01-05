import {Component, OnChanges, OnInit} from '@angular/core';
import {ProductService} from './product.service';
import {Product} from '../model/product';
import {Observable} from 'rxjs/Observable';
import {FileUploader} from 'ng2-file-upload';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {LoggerService} from '../utils/logger.service';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';

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
    imageSpots = [1, 2, 3, 4];
    constructor(private productService: ProductService,
                private logger: LoggerService,
                private route: ActivatedRoute,
                private router: Router,
                private fb: FormBuilder) {
        this.createForm();
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
                    image1: p.images['1'] || '1',
                    image2: p.images['2'] || '2',
                    image3: p.images['3'] || '3',
                    image4: p.images['4'] || '4'
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
               image1: '',
               image2: '',
               image3: '',
               image4: ''
            }),
            link: ''
        })
    }

/*    addInput() {
        let number = this.attr.length + 1;
        this.attr.push({'key': '', 'value': ''});
    }

    removeInput(i) {
        let j = this.attr.indexOf(i);
        this.attr.splice(j, 1);
    }*/

    onSubmit() {
        alert(22);
        let product = new Product(
            0,
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

    uploadImage(bt, name, spot) {
        alert('name: ' + name + ', spot:' + spot);
        // bt.click();
    }

    removeImage(spot) {
        let name = 'image' + spot;
        // this.productForm.get('images').get(name).setValue('')
    }
}
