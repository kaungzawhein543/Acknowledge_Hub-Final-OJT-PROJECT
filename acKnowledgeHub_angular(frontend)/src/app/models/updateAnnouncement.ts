import { Category } from "./category";

export interface updateAnnouncement {
    announcedAt: string;
    id: number;
    title: string;
    description: string;
    category: Category;
    createdStaffId : number;
    status: string;
    staffInGroups : [];
    created_at: Date;
    file: string;
    published : boolean;
    scheduleAt: Date;
    groupStatus: number;
    staff :[];
    group : [];
}