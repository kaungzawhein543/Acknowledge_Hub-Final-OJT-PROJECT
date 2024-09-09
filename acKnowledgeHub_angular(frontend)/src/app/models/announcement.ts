import { Category } from "./category";
import { Staff } from "./user.model";

export interface announcement {
  id: number;
  title: string;
  description: string;
  category: Category;
  createStaff: Staff;
  status: string;
  created_at: Date;
  scheduleAt: Date;
  groupStatus: number;
  [key: string]: any;
  oldVersionStaff :[];
  oldVersionGroup : [];
  file?: File;  // File is handled separately
}

export interface AnnouncementStaffCountDTO {
  announcementId: number;
  title: string;
  created_at: string; // or Date if you're handling date objects
  staffCount: number;
}

export interface StaffProfileDTO {
  id: number;
  name: string;
  companyStaffId: string;
  email: string;
  status: string;
  role: string; // or Role if you have an enum for Role
  position: string;
  department: string;
  company: string;
  createdAt: Date;
  chatId: string;
}
