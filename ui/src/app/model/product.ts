export class Product {
    id: number;
    name: string;
    sku: string;
    category: number;
    parent: number;
    attributes: object[];
    content: string;
    price: number;
    sellPrice: number;
    asin: string;
    stock: number;
    show: boolean;
    images: object;
    link: string;


    constructor(id: number, name: string, sku: string, category: number,
                parent: number, attributes: Object[], content: string, price: number,
                sellPrice: number, asin: string, stock: number, show: boolean,
                images: Object, link: string) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.parent = parent;
        this.attributes = attributes;
        this.content = content;
        this.price = price;
        this.sellPrice = sellPrice;
        this.asin = asin;
        this.stock = stock;
        this.show = show;
        this.images = images;
        this.link = link;
    }
}
