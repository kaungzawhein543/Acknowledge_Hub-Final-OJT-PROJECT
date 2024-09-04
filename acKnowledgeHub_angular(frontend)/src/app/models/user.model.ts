export interface User {
    id: number;
    password: string;
    role: string;
    status: string;
    createdAt: Date;
    staff: Staff;
  }
  
  export interface Staff {
    name: string;
    staffId: string;
    email: string;
    address: string;
    createdAt: Date;
    status: string;
    position: string;
    department: string | null
  }
  