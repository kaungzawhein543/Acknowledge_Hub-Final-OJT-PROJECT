import { Category } from "./category";
import { Staff } from "./user.model";

export interface announcement {
  id: number;
  title: string;
  description: string;
  category: Category;
  createStaff: Staff;
  status: string;
  createdAt: string;
  scheduleAt: Date;
  forRequest: number;
  groupStatus: number;
  [key: string]: any;
  oldVersionStaff: [];
  oldVersionGroup: [];
  file?: File;  // File is handled separately
}

export interface AnnouncementStaffCountDTO {
  announcementId: number;
  title: string;
  created_at: string; // or Date if you're handling date objects
  staffCount: number;
}

export interface AnnouncementStatsDTO {
  totalAnnouncements: number;
  publishedAnnouncements: number;
  unpublishedAnnouncements: number;
}



export interface AnnouncementListDTO {
  id: number;
  title: string;
  description: string;
  createStaff: string; // Assuming `createStaff.name` is a string. Adjust if it's an object.
  category: string; // Assuming `category.name` is a string. Adjust if it's an object.
  status: string;
  created_at: Date; // Ensure this matches the date format from your backend
  scheduleAt: Date; // Ensure this matches the date format from your backend
  groupStatus: string; // Adjust type if needed
  [key: string]: any;
}

export interface MonthlyCountDTO {
  year: number;
  month: number;
  count: number;
}
