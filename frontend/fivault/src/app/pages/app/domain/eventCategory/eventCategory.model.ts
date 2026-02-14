export interface EventCategoryNode {
  id: string;
  name: string;
  description?: string;
  parentEventCategoryId: string;
  children: EventCategoryNode[];
}

export interface EventCategory {
  eventCategoryId: string;
  name: string;
  description?: string;
  parentEventCategoryId: string | any
}