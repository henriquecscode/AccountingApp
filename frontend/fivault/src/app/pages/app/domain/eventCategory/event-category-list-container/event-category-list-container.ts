import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventCategoryListNode } from '../event-category-list-node/event-category-list-node';
import { EventCategory, EventCategoryNode } from '../eventCategory.model'

@Component({
  selector: 'app-event-category-list-container',
  imports: [CommonModule, EventCategoryListNode],
  templateUrl: './event-category-list-container.html',
  styleUrl: './event-category-list-container.scss',
  standalone: true
})
export class EventCategoryListContainer {
  @Input({ required: true }) categories!: EventCategoryNode[];
  @Output() addChild = new EventEmitter<string>();
  @Output() editNode = new EventEmitter<EventCategory>();  
  @Output() deleteNode = new EventEmitter<string>();

  onAddChild(categoryId: string) {
    this.addChild.emit(categoryId);
  }

  onEditNode(data: EventCategory) {
    this.editNode.emit(data);
  }  

  onDeleteNode(eventCategoryId: string){
    this.deleteNode.emit(eventCategoryId);
  }
}
