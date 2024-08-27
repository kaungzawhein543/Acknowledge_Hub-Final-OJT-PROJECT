export interface User {
    id: number;
    name: string;
    password: string;
    role?: string;  // File is handled separately
    status: string;
    email: string;
    createdAt: string;
    staff : string;
    groups: string;
  }
  