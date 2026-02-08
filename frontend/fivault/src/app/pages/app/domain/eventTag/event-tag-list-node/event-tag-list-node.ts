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
  @Output() deleteNode = new EventEmitter<string>();

  expanded = true;

  toggle() {
    this.expanded = !this.expanded;
  }

  onClickAddChild() {
    this.addChild.emit(this.tag.id);
  }

  onAddChild(eventTagId: string) {
    this.addChild.emit(eventTagId);
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

  onClickDeleteNode() {
    this.deleteNode.emit(this.tag.id)
  }

  onDeleteNode(eventTagId: string){
    this.deleteNode.emit(eventTagId);
  }

  remove() {
    // Handled by parent in real implementation
    alert('Delete logic goes here');
  }
}
