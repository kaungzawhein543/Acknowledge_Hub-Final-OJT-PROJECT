import { announcement } from "./announcement";

export class Notification {
  readonly id: number;
  readonly title: string;
  readonly description: string;
  readonly url : string;
  readonly created_at : string;
  readonly staffId: string;
   checked : boolean;
   announcementDetails : announcement;
  readonly groupIds?: number[];
  public status?: string;
  
  constructor(
    id: number,
    title: string,
    description: string,
    url : string,
    created_at : string,
    staffId: string,
    checked : boolean,
    announcementDetails : announcement,
    groupIds?: number[]
) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.url = url;
    this.created_at = created_at;
    this.staffId = staffId;
    this.checked = checked;
    this.announcementDetails = announcementDetails;
    this.groupIds = groupIds;
}
}