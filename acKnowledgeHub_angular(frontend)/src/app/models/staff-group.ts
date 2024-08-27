import { Department } from "./Department";

export interface StaffGroup {
    staffId: number;
    name: string;
    position: string;
    department: Department;
}