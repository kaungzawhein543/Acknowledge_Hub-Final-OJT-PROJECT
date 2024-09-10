import { Role } from "./ROLE";

export interface Staff {
  selected: boolean;
  id: number;
  staffId: string;
  name: string;
  position: string;
  groupId: number; // To associate staff with a group
  department: {
    id: number;
    name: string;
  };
  company: {
    id: number;
    name: string;
  };
}

export interface staffList {
  id: number;
  companyStaffId: string;
  name: string;
  email: string;
  role: Role;
  position: string;
  department: string;
  company: string;
  status: string;
  [key: string]: any;
}

