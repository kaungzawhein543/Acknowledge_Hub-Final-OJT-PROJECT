
export interface Staff {
  selected: boolean;
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

