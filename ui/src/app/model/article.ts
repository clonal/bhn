
export class Article {
    id: number;
    menu: number;
    title: string;
    desc: string;
    content: string;
    order: number;
    date: string;


    constructor(id: number, menu: number, title: string, desc: string,
                content: string, order: number, date: string) {
        this.id = id;
        this.menu = menu;
        this.title = title;
        this.desc = desc;
        this.content = content;
        this.order = order;
        this.date = date;
    }
}
