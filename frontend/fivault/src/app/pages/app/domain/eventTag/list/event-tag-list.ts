import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { EventTagNode } from '../eventTag.model';
import { CommonModule } from '@angular/common';
import { EventTagListContainer } from '../event-tag-list-container/event-tag-list-container';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventTagService } from '../../../../../services/eventTag.service';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../../util/error-localization';
import { map, Observable, of, catchError, shareReplay } from 'rxjs';

interface ViewModel {
  tags: EventTagNode[];
  loading: boolean;
  error: string;
}

@Component({
  selector: 'app-event-tag-list',
  imports: [CommonModule, EventTagListContainer, ReactiveFormsModule],
  templateUrl: './event-tag-list.html',
  styleUrl: './event-tag-list.scss',
  standalone: true
})
export class EventTagList implements OnInit {
  MAX_INPUT_LENGTH = 255;

  viewModel$!: Observable<ViewModel>;
  owner: string;
  domainSlug: string;
  createTagForm: FormGroup;
  submitted = false;
  saving = false;
  backendError = '';
  creating = false;
  parentEventTagId: string | null = null;  // Track parent when creating child tag

  private errorHandler = new BackendErrorLocalizationHandler(
    [],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@event-tag-create-backend-error-unknown:Event tag creation failed with error ${error}. Please try again`
    )
  );

  constructor(
    private eventTagService: EventTagService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
    this.createTagForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(this.MAX_INPUT_LENGTH)]],
      description: ['', [Validators.maxLength(this.MAX_INPUT_LENGTH)]]
    });
  }

  ngOnInit() {
    if (!this.owner || !this.domainSlug) {
      this.viewModel$ = of({
        tags: [],
        creating: false,
        loading: false,
        error: 'Invalid route parameters'
      });
    } else {
      this.loadTags();
    }
  }

  get name() {
    return this.createTagForm.get("name");
  }

  get description() {
    return this.createTagForm.get("description");
  }

  loadTags() {
    this.viewModel$ = this.eventTagService.getList(this.owner, this.domainSlug).pipe(
      map(result => ({
        tags: this.listToTree(result.eventTagList),
        loading: false,
        error: ''
      })),
      catchError(err => {
        console.error('Error loading tags:', err);
        return of({
          tags: [],
          loading: false,
          error: 'Failed to load tags. Please try again.'
        });
      }),
      shareReplay(1)
    );
  }

  startCreate(parentId: string | null = null) {
    this.creating = true;
    this.parentEventTagId = parentId;
    this.submitted = false;
    this.backendError = '';
    this.createTagForm.reset();
  }

  cancelCreate() {
    this.creating = false;
    this.parentEventTagId = null;
    this.backendError = '';
    this.createTagForm.reset();
  }

  saveCreate() {
    this.submitted = true;
    this.backendError = '';

    if (this.createTagForm.invalid) {
      this.createTagForm.markAllAsTouched();
      return;
    }

    this.saving = true;

    const { name, description } = this.createTagForm.value;

    this.eventTagService.create(this.owner, this.domainSlug, name, description, this.parentEventTagId).subscribe({
      next: (response) => {
        console.log('Event Tag create success', response);
        this.saving = false;
        this.creating = false;
        this.createTagForm.reset();
        this.submitted = false;

        // Create the new tag node
        const newTag: EventTagNode = {
          id: response.eventTag.eventTagId,
          name: response.eventTag.name,
          description: response.eventTag.description,
          children: []
        };

        // Update the viewModel by adding the new tag to the existing tags
        var parentEventTagId = response.eventTag.eventTagParentId
        this.viewModel$ = this.viewModel$.pipe(
          map(vm => ({
            ...vm,
            tags: parentEventTagId
              ? this.insertChildTag(vm.tags, parentEventTagId, newTag)
              : [...vm.tags, newTag]
          }))
        );
        
      },
      error: (err) => {
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        const paramsString = params ? JSON.stringify(params, null, 2) : '';
        this.backendError = this.errorHandler.localize(errorCode, paramsString);
        this.saving = false;
        this.cdr.detectChanges();
      }
    });
  }

  private listToTree(list: any[]): EventTagNode[] {
    // Build a map for quick lookup
    const map = new Map<string, EventTagNode>();
    const roots: EventTagNode[] = [];

    // First pass: create all nodes
    list.forEach(item => {
      map.set(item.eventTagId, {
        id: item.eventTagId,
        name: item.name,
        description: item.description,
        children: []
      });
    });

    // Second pass: build the tree
    list.forEach(item => {
      const node = map.get(item.eventTagId)!;
      if (item.eventTagParentId) {
        const parent = map.get(item.eventTagParentId);
        if (parent) {
          parent.children.push(node);
        } else {
          // Parent not found, treat as root
          roots.push(node);
        }
      } else {
        // No parent, it's a root node
        roots.push(node);
      }
    });

    return roots;
  }

  private insertChildTag(
    nodes: EventTagNode[],
    parentId: string,
    newTag: EventTagNode
  ): EventTagNode[] {

    return nodes.map(node => {

      // ✅ If this is the parent → add child here
      if (node.id === parentId) {
        return {
          ...node,
          children: [...(node.children ?? []), newTag]
        };
      }

      // ✅ Otherwise recurse into children
      if (node.children?.length) {
        return {
          ...node,
          children: this.insertChildTag(node.children, parentId, newTag)
        };
      }

      return node;
    });
  }

}