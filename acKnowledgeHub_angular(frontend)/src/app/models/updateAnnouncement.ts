import { Category } from "./category";

export interface updateAnnouncement {
    id: number;
    title: string;
    description: string;
    category: Category;
    createdStaffId : number;
    status: string;
    staffInGroups : [];
    created_at: Date;
    file: string;
    scheduleAt: Date;
    groupStatus: number;
    staff :[];
    group : [];
}