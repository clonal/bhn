
export class Product {
    id: number;
    name: string;
    desc: string;
    category: number[];
    sku: number[];
    amazonLink: string;

    constructor(id: number, name: string, desc: string, category: number[], sku: number[], amazonLink: string) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.sku = sku;
        this.amazonLink = amazonLink;
    }
}
