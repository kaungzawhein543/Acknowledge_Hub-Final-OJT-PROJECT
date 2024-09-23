import { Component, OnInit } from '@angular/core';
import { Category } from '../../models/category';
import { CategoryService } from '../../services/category.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-update-category',
  templateUrl: './update-category.component.html',
  styleUrls: ['./update-category.component.css']
})
export class UpdateCategoryComponent implements OnInit {
  id!: number;
  category: Category = new Category(); 
  submitted = false;

  constructor(
    private categoryService: CategoryService, 
    private route: ActivatedRoute,
    private router: Router,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    this.id = +this.route.snapshot.params['id'];

    // Fetch the category to update
    this.categoryService.getById(this.id).subscribe({
      next: (data) => {
        console.log(data)
        this.category = data;
      },
      error: (e) => {
        console.error('Error fetching category:', e);
      }
    });
  }

  // Function to go back to the list page without showing success toast
  goBack(): void {
    this.router.navigate(['/list-category']);
  }

  // Function to handle form submission and updating the category
  saveCategory(): void {
    this.categoryService.update(this.id, this.category).subscribe({
      next: (updatedCategory) => {
        console.log('Category updated successfully:', updatedCategory);
        this.showSuccessToast(); // Only show toast when update is successful
        this.router.navigate(['/acknowledgeHub/list-category']); // Redirect to the category list after update
      },
      error: (err) => {
        console.error('Error updating category:', err);
      }
    });
  }

  showSuccessToast(): void {
    this.toastService.showToast('Updated Category successfully!', 'success');
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.submitted = false;
      this.saveCategory();  // Call the save method if the form is valid
    } else {
      this.submitted = true;
      console.log('Invalid form');
    }
  }
  
}
