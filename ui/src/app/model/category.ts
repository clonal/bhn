export class Category {
    id: number;
    name: string;
    desc: string;
    department: number;
    banner: string;


    constructor(id: number, name: string, desc: string, department: number, banner: string) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.department = department;
        this.banner = banner;
    }
}
