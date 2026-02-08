import { Component, Input } from '@angular/core';
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

  expanded = false;
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

  addChild() {
    const newChild: EventTagNode = {
      id: crypto.randomUUID(),
      name: 'New tag',
      description: '',
      children: []
    };

    this.tag.children.push(newChild);
    this.expanded = true;

    // TODO: call backend create
  }

  remove() {
    // Handled by parent in real implementation
    alert('Delete logic goes here');
  }
}
