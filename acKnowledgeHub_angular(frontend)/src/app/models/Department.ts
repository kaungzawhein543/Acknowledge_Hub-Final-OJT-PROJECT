import { Company } from "./Company";

export interface Department {
    id: number;
    name: string;
    company: Company;
}