export interface announcement {
    id: number;
    title: string;
    description: string;
    file?: File;  // File is handled separately
  }

  export interface AnnouncementStaffCountDTO {
    announcementId: number;
    title: string;
    createdAt: string; // or Date if you're handling date objects
    staffCount: number;
  }
  