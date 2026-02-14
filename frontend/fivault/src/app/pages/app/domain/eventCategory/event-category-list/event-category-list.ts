import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { EventCategory, EventCategoryNode } from '../eventCategory.model';
import { CommonModule } from '@angular/common';
import { EventCategoryListContainer } from '../event-category-list-container/event-category-list-container';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { EventCategoryService } from '../../../../../services/eventCategory.service';
import { BackendErrorLocalizationHandler, ErrorMessage } from '../../../../../util/error-localization';
import { map, Observable, of, catchError, shareReplay } from 'rxjs';

interface ViewModel {
  categories: EventCategoryNode[];
  loading: boolean;
  error: string;
}


@Component({
  selector: 'app-event-category-list',
  imports: [CommonModule, EventCategoryListContainer, ReactiveFormsModule],
  templateUrl: './event-category-list.html',
  styleUrl: './event-category-list.scss',
  standalone: true
})
export class EventCategoryList implements OnInit {
  MAX_INPUT_LENGTH = 255;

  viewModel$!: Observable<ViewModel>;
  owner: string;
  domainSlug: string;
  createCategoryForm: FormGroup;
  submitted = false;
  saving = false;
  creating = false;
  editing = false;
  backendError = '';
  eventCategoryId: string | null = null
  parentEventCategoryId: string | null = null;  // Track parent when creating child category

  private errorHandler = new BackendErrorLocalizationHandler(
    [],
    new ErrorMessage('UNKNOWN_ERROR', (error) =>
      $localize`:@@event-category-create-backend-error-unknown:Event category creation failed with error ${error}. Please try again`
    )
  );

  constructor(
    private eventCategoryService: EventCategoryService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.owner = this.route.snapshot.paramMap.get('owner')!;
    this.domainSlug = this.route.snapshot.paramMap.get('domainSlug')!;
    this.createCategoryForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(this.MAX_INPUT_LENGTH)]],
      description: ['', [Validators.maxLength(this.MAX_INPUT_LENGTH)]]
    });
  }

  ngOnInit() {
    if (!this.owner || !this.domainSlug) {
      this.viewModel$ = of({
        categories: [],
        loading: false,
        error: 'Invalid route parameters'
      });
    } else {
      this.loadCategories();
    }
  }

  get name() {
    return this.createCategoryForm.get("name");
  }

  get description() {
    return this.createCategoryForm.get("description");
  }

  loadCategories() {
    this.viewModel$ = this.eventCategoryService.getList(this.owner, this.domainSlug).pipe(
      map(result => ({
        categories: this.listToTree(result.eventCategoryList),
        loading: false,
        error: ''
      })),
      catchError(err => {
        console.error('Error loading categories:', err);
        return of({
          categories: [],
          loading: false,
          error: 'Failed to load categories. Please try again.'
        });
      }),
      shareReplay(1)
    );
  }

  startCreate(parentId: string | null = null) {
    this.creating = true;
    this.editing = false;
    this.startForms(null, parentId);
  }

  startEdit(data: EventCategory) {
    this.editing = true;
    this.creating = false;
    this.startForms(data.eventCategoryId, data.parentEventCategoryId);
    this.createCategoryForm.patchValue({
      name: data.name,
      description: data.description
    });
  }

  startForms(eventCategoryId: string | null = null, parentEventCategoryId: string | null = null) {
    this.eventCategoryId = eventCategoryId;
    this.parentEventCategoryId = parentEventCategoryId;
    this.submitted = false;
    this.backendError = '';
    this.createCategoryForm.reset();

  }

  cancelCreate() {
    this.parentEventCategoryId = null;
    this.backendError = '';
    this.createCategoryForm.reset();
    this.creating = false;
    this.editing = false;
  }

  saveForms() {
    this.submitted = true;
    this.backendError = '';

    if (this.createCategoryForm.invalid) {
      this.createCategoryForm.markAllAsTouched();
      return;
    }

    this.saving = true;

    const { name, description } = this.createCategoryForm.value;

    if (this.creating) {
      this.eventCategoryService.create(this.owner, this.domainSlug, name, description, this.parentEventCategoryId).subscribe({
        next: (response) => {
          console.log('Event Category create success', response);
          this.saving = false;
          this.creating = false;
          this.editing = false;
          this.createCategoryForm.reset();
          this.submitted = false;

          // Create the new category node
          const newCategory: EventCategoryNode = {
            id: response.eventCategory.eventCategoryId,
            name: response.eventCategory.name,
            description: response.eventCategory.description,
            parentEventCategoryId: response.eventCategory.parentEventCategoryId,
            children: []
          };

          // Update the viewModel by adding the new category to the existing categories
          var parentEventCategoryId = response.eventCategory.parentEventCategoryId
          this.viewModel$ = this.viewModel$.pipe(
            map(vm => ({
              ...vm,
              categories: parentEventCategoryId
                ? this.insertChildCategory(vm.categories, parentEventCategoryId, newCategory)
                : [newCategory, ...vm.categories]
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
          this.udpateVM();
        }
      });
    }
    if (this.editing) {
      this.eventCategoryService.update(this.owner, this.domainSlug, this.eventCategoryId!, name, description, this.parentEventCategoryId).subscribe({
        next: (response) => {
          console.log('Event Category update success', response);
          this.saving = false;
          this.creating = false;
          this.editing = false;
          this.createCategoryForm.reset();
          this.submitted = false;


          // Create the new category node
          const newCategory: EventCategoryNode = {
            id: response.eventCategory.eventCategoryId,
            name: response.eventCategory.name,
            description: response.eventCategory.description,
            parentEventCategoryId: response.eventCategory.parentEventCategoryId,
            children: []
          };

          // Update static details
          // Does not cover changes in hierarchy // TODO
          this.viewModel$ = this.viewModel$.pipe(
            map(vm => ({
              ...vm,
              categories: this.updateCategoryNode(vm.categories, newCategory)
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
          this.udpateVM();
        }
      });
    }
  }

  deleteNode(eventCategoryId: string) {
    this.eventCategoryService.delete(this.owner, this.domainSlug, eventCategoryId).subscribe({
      next: (response) => {
        this.viewModel$ = this.viewModel$.pipe(
          map(vm => ({
            ...vm,
            categories: this.deleteCategoryNode(vm.categories, eventCategoryId)
          }))
        );
        this.cdr.detectChanges();
      },
      error: (err) => {
        const errorCode: string = err.error?.errorCode || 'UNKNOWN_ERROR';
        const params: any = err.error?.params;
        const paramsString = params ? JSON.stringify(params, null, 2) : '';
        this.backendError = this.errorHandler.localize(errorCode, paramsString);
        this.saving = false;
        this.cdr.detectChanges();
        this.udpateVM();
      }
    })
  }

  private listToTree(list: EventCategory[]): EventCategoryNode[] {
    // Build a map for quick lookup
    const map = new Map<string, EventCategoryNode>();
    const roots: EventCategoryNode[] = [];

    // First pass: create all nodes
    list.forEach(item => {
      map.set(item.eventCategoryId, {
        id: item.eventCategoryId,
        name: item.name,
        description: item.description,
        parentEventCategoryId: item.parentEventCategoryId,
        children: []
      });
    });

    // Second pass: build the tree
    list.forEach(item => {
      const node = map.get(item.eventCategoryId)!;
      if (item.parentEventCategoryId) {
        const parent = map.get(item.parentEventCategoryId);
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

  private insertChildCategory(
    nodes: EventCategoryNode[],
    parentId: string,
    newCategory: EventCategoryNode
  ): EventCategoryNode[] {

    return nodes.map(node => {

      // ✅ If this is the parent → add child here
      if (node.id === parentId) {
        return {
          ...node,
          children: [newCategory, ...(node.children ?? [])]
        };
      }

      // ✅ Otherwise recurse into children
      if (node.children?.length) {
        return {
          ...node,
          children: this.insertChildCategory(node.children, parentId, newCategory)
        };
      }

      return node;
    });
  }

  private updateCategoryNode(
    nodes: EventCategoryNode[],
    updatedCategory: EventCategoryNode
  ): EventCategoryNode[] {

    return nodes.map(node => {

      // ✅ Found the node → replace its editable fields
      if (node.id === updatedCategory.id) {
        return {
          ...node,
          name: updatedCategory.name,
          description: updatedCategory.description
          // keep children untouched
        };
      }

      // ✅ Otherwise recurse into children
      if (node.children?.length) {
        return {
          ...node,
          children: this.updateCategoryNode(node.children, updatedCategory)
        };
      }

      return node;
    });
  }

  private deleteCategoryNode(
    nodes: EventCategoryNode[],
    categoryId: string
  ): EventCategoryNode[] {

    var newNodes = nodes
      // ✅ Remove the node if it matches
      .filter(node => node.id !== categoryId)

      // ✅ Recurse into children
      .map(node => ({
        ...node,
        children: node.children?.length
          ? this.deleteCategoryNode(node.children, categoryId)
          : node.children
      }));
    return newNodes;
  }



  private udpateVM() {
    this.viewModel$ = this.viewModel$.pipe(
      map(vm => vm)
    );
  }

}