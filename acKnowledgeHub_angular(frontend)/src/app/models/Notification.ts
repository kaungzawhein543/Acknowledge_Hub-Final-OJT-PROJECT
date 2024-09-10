export class Notification {
  readonly id: number;
  readonly title: string;
  readonly description: string;
  readonly staffId: string;
  readonly groupIds?: number[];
  public status?: string;
  
  constructor(
    id: number,
    title: string,
    description: string,
    staffId: string,
    groupIds?: number[]
) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.staffId = staffId;
    this.groupIds = groupIds;
}
}