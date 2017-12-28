
export class Item {
    id: number;
    name: string;
    desc: string;
    category: number[];
    product: number[];
    amazonLink: string;

    constructor(id: number, name: string, desc: string, category: number[], product: number[], amazonLink: string) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.product = product;
        this.amazonLink = amazonLink;
    }
}
