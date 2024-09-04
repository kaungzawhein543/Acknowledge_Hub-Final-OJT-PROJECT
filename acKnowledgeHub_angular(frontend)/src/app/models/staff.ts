// import { Role } from "./ROLE";

// export class Staff {
//     companyStaffId?: string;
//     email?: string;
//     name?: string;
//     role?: Role;
//     positionId?: number;
//     departmentId?: number;
//     companyId?: number;
// }
export interface Staff {
  id: number;
  staffId: string;
  name: string;
  position: string;
  groupId: number; // To associate staff with a group
  department: {
    id: number;
    name: string;
  };
  company: {
    id: number;
    name: string;
  };
}

