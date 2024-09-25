export class NotedUser {
    staffId!: string;
    name?: string;
    departmentName?: string;
    companyName?: string;
    positionName?: string;
    createdAt?: Date;
    notedAt?: Date;
    email?: string;
    [key: string]: any;
}