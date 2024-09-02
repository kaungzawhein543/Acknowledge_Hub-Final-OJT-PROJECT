import { Category } from "./category";
import { Staff} from "./user.model";

export interface announcement {
    id: number;
    title: string;
    description: string;
    file?: File;  
    category: Category; 
    createStaff?:Staff;
    status : string;
    created_at: Date;   
    scheduleAt: Date; 
    [key: string]: any;
  }
