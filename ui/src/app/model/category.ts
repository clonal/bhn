export class Category {
    id: number;
    name: string;
    desc: string;
    parent: number;
    banner: string;


    constructor(id: number, name: string, desc: string, parent: number, banner: string) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.parent = parent;
        this.banner = banner;
    }
}
