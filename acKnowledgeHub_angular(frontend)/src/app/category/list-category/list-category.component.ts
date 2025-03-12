import { Component, OnInit, ViewChild } from '@angular/core';
import { Category } from '../../models/category';
import { CategoryService } from '../../services/category.service'; 
import { Router } from '@angular/router';
import { ConfirmationModalComponent } from '../../confirmation-modal/confirmation-modal.component';
import { trigger, style, transition, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-list-category',
  templateUrl: './list-category.component.html',
  styleUrls: ['./list-category.component.css'],
  animations: [
    trigger('cardAnimation', [
      transition(':enter', [
        query('.card', [
          style({ opacity: 0, transform: 'translateY(20px)' }),
          stagger(200, [
            animate('500ms ease-out', style({ opacity: 1, transform: 'translateY(0)' })),
          ])
        ]),
      ]),
    ]),
  ],
})
export class ListCategoryComponent implements OnInit {
  private itemIdToDelete: number | null = null;
  categories: Category[] = [];
  @ViewChild('confirmationModal') modal!: ConfirmationModalComponent;
  constructor(private categoryService: CategoryService, private router: Router) {}

  ngOnInit(): void {
    this.getCategories();
  }
  
  private getCategories(): void {
    this.categoryService.getAll()
      .subscribe({
        next: (data) => {
          console.log(data);
          this.categories = data;
        },
        error: (e) => console.error(e)
      });
  }

  updateCategory(id: number): void {
    this.router.navigate(['acknowledgeHub/update-category', id]);
  }

  openDeleteModal(itemId: number) {
    this.itemIdToDelete = itemId;
    this.modal.open();
  }

  onDeleteConfirmed() {
    if (this.itemIdToDelete) {
      // Call the delete method with the stored itemId
      this.softDeleteCategory(this.itemIdToDelete);
      this.itemIdToDelete = null; // Reset the itemId
    }
  }

  softDeleteCategory(id: number): void {
      this.categoryService.softDelete(id)
        .subscribe({
          next: () => {
            this.getCategories(); // Refresh the list after deletion
          },
          error: (e) => console.error(e)
        });
  }
}
