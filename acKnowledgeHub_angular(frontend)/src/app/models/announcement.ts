import { Category } from "./category";
import { Staff } from "./user.model";

export interface announcement {
    id: number;
    title: string;
    description: string;
    groups : [];
    staffs :[];
    category: Category; 
    createStaff : Staff;
    status :string;
    created_at:Date;
    scheduleAt : Date;
    groupStatus : number;
    [key: string]: any;
    file?: File;  // File is handled separately
  }
  