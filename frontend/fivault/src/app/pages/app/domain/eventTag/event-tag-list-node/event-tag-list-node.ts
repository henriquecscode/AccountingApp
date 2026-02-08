import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventTagNode } from '../eventTag.model';
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

  expanded = true;
  editing = false;

  editName = '';
  editDescription = '';

  toggle() {
    this.expanded = !this.expanded;
  }

  startEdit() {
    this.editing = true;
    this.editName = this.tag.name;
    this.editDescription = this.tag.description ?? '';
  }

  cancelEdit() {
    this.editing = false;
  }

  saveEdit() {
    this.tag.name = this.editName;
    this.tag.description = this.editDescription;
    this.editing = false;

    // TODO: call backend update
  }

    onAddChild(childId?: string) {
    // If childId is provided (from nested child), bubble it up
    // Otherwise emit this tag's ID
    this.addChild.emit(childId || this.tag.id);
  }

  remove() {
    // Handled by parent in real implementation
    alert('Delete logic goes here');
  }
}
