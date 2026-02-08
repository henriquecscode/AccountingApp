import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EventTagListNode } from '../event-tag-list-node/event-tag-list-node';
import { EventTagNode } from '../eventTag.model';

@Component({
  selector: 'app-event-tag-list-container',
  imports: [CommonModule, EventTagListNode],
  templateUrl: './event-tag-list-container.html',
  styleUrl: './event-tag-list-container.scss',
  standalone: true
})
export class EventTagListContainer {
  @Input({ required: true }) tags!: EventTagNode[];
  @Output() addChild = new EventEmitter<string>();

  onAddChild(tagId: string) {
    this.addChild.emit(tagId);
  }

}
