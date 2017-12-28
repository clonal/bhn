export class Feedback {
    id: number;
    category: number;
    market: number;
    name: string;
    email: string;
    order: string;
    item: number;
    suggest: string;
    image: string;
    date: string;
    ip: string;

    constructor(id: number, category: number, market: number,
                name: string, email: string, order: string,
                item: number, suggest: string, image: string,
                date: string, ip: string) {
        this.id = id;
        this.category = category;
        this.market = market;
        this.name = name;
        this.email = email;
        this.order = order;
        this.item = item;
        this.suggest = suggest;
        this.image = image;
        this.date = date;
        this.ip = ip;
    }
}
