import { Company } from "./Company";
import { Department } from "./Department";
import { Position } from "./Position";

export interface StaffGroup {
    staffId: number;
    name: string;
    position: Position;
    department: Department;
    company: Company;
    photoPath: string;
}