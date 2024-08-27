export interface User {
    id: number;
    password: string;
    role: string;
    status: string;
    createdAt: Date;
    staff: Staff;
  }
  
  export interface Staff {
    id: number;
    name: string;
    staffId: string;
    email: string;
    ph1: string;
    ph2: string;
    address: string;
    createdAt: Date;
    status: string;
  }
  