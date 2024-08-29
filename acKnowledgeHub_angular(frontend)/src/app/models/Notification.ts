export class Notification {
    id: number;
    title: string;
    description: string;
    
    staffId: string;
  
    constructor(id: number,title: string, description: string, staffId: string ) {
      this.id = id;
      this.title= title;
      this.description = description;
      
      this.staffId = staffId;
    }
  }