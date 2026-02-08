export interface EventTagNode {
  id: string;
  name: string;
  description?: string;
  parentEventTagId: string;
  children: EventTagNode[];
}

export interface EventTag {
  eventTagId: string;
  name: string;
  description?: string;
  parentEventTagId: string | any
}