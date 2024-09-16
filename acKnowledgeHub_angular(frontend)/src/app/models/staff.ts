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

export interface StaffProfileDTO {
  id: number;
  name: string;
  companyStaffId: string;
  email: string;
  status: string;
  role: string; // or Role if you have an enum for Role
  photoPath: string;
  position: string;
  department: string;
  company: string;
  createdAt: Date;
  chatId: string;
  monthlyCount: { [month: string]: number };
}

export interface StaffSummaryCount{
  totalStaff :number;
  activeStaff :number;
  inactiveStaff :number
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

