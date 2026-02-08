import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventTag, EventTagNode } from '../eventTag.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-event-tag-list-node',
  imports: [CommonModule, FormsModule],
  templateUrl: './event-tag-list-node.html',
  styleUrl: './event-tag-list-node.scss',
})
export class EventTagListNode {
  @Input({ required: true }) tag!: EventTagNode;
  @Output() addChild = new EventEmitter<string>();
  @Output() editNode = new EventEmitter<EventTag>();

  expanded = true;

  toggle() {
    this.expanded = !this.expanded;
  }

  onClickAddChild() {
    this.addChild.emit(this.tag.id);
  }

  onAddChild(childId?: string) {
    // If childId is provided (from nested child), bubble it up
    // Otherwise emit this tag's ID
    this.addChild.emit(childId || this.tag.id);
  }

  onClickEditNode() {
    this.editNode.emit({
      eventTagId: this.tag.id,
      name: this.tag.name,
      description: this.tag.description,
      parentEventTagId: this.tag.parentEventTagId
    });
  }
  onEditNode(data: EventTag) {
    this.editNode.emit(data);
  }

  remove() {
    // Handled by parent in real implementation
    alert('Delete logic goes here');
  }
}
