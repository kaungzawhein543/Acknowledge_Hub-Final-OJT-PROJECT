export interface announcementList {
    id: number;
    title: string;
    description: string;
    createdAt: Date;
    createStaff: string;
    file: string;
    category: string;
    [key: string]:any;
    
}

export interface listAnnouncement {
    id: number;
    title: string;
    description: string;
    createStaff: string;
    category: string;
    status: string;
    created_at: Date;
    scheduleAt: Date;
    groupStatus: number;
    [key: string]: any;
}