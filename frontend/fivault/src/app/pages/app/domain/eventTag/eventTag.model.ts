export interface EventTagNode {
  id: string;
  name: string;
  description?: string;
  children: EventTagNode[];
}

export interface EventTag {
  eventTagId: string;
  name: string;
  description: string;
  eventTagParentId: string | any
}