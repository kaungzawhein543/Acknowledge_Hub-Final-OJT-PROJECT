import { Staff } from "./staff";

export interface Group{
    id : number;
    name: string;
    status?: string;
    createdAt: Date;
    staff : Staff[];
    selected : boolean;
}