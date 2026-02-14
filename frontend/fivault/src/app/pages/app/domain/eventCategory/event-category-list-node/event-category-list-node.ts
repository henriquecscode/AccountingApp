import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventCategory, EventCategoryNode } from '../eventCategory.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-event-category-list-node',
  imports: [CommonModule, FormsModule],
  templateUrl: './event-category-list-node.html',
  styleUrl: './event-category-list-node.scss',
})
export class EventCategoryListNode {
  @Input({ required: true }) category!: EventCategoryNode;
  @Output() addChild = new EventEmitter<string>();
  @Output() editNode = new EventEmitter<EventCategory>();
  @Output() deleteNode = new EventEmitter<string>();

  expanded = true;

  toggle() {
    this.expanded = !this.expanded;
  }

  onClickAddChild() {
    this.addChild.emit(this.category.id);
  }

  onAddChild(eventCategoryId: string) {
    this.addChild.emit(eventCategoryId);
  }

  onClickEditNode() {
    this.editNode.emit({
      eventCategoryId: this.category.id,
      name: this.category.name,
      description: this.category.description,
      parentEventCategoryId: this.category.parentEventCategoryId
    });
  }
  onEditNode(data: EventCategory) {
    this.editNode.emit(data);
  }

  onClickDeleteNode() {
    this.deleteNode.emit(this.category.id)
  }

  onDeleteNode(eventCategoryId: string){
    this.deleteNode.emit(eventCategoryId);
  }

  remove() {
    // Handled by parent in real implementation
    alert('Delete logic goes here');
  }
}
