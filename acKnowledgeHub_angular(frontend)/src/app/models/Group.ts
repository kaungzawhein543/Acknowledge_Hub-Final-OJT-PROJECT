import { Staff } from "./staff";
import { StaffGroup } from "./staff-group";

export interface Group{
    id : number;
    name: string;
    status?: string;
    createdAt: Date;
    staff : StaffGroup[];
    selected : boolean;
}