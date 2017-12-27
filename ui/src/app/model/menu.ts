
export class Menu {
    id: number;
    name: string;
    order: number;
    parent: number;
    content: string;
    desc: string;
    banner: {};
    children: Menu[];
    required = false;

    constructor(id: number, name: string, order: number,
                parent: number, content: string, desc: string,
                banner: {}, children: Menu[], required?: boolean) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.parent = parent;
        this.content = content;
        this.desc = desc;
        this.banner = banner;
        this.children = children;
        this.required = required ? required : false;
    }
}
