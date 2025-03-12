export interface staffNotedAnnouncement {
    id: number;
    title: string;
    description: string;
    createdAt: Date;
    notedAt: Date;
    file: string;
    createStaff: string;
    [key: string]: any;
}