import { Component, OnInit } from '@angular/core';
import { Category } from '../../models/category';
import { CategoryService } from '../../services/category.service';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { ToastService } from '../../services/toast.service';
import { error } from 'console';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-category',
  templateUrl: './add-category.component.html',
  styleUrls: ['./add-category.component.css'] // Note: It's styleUrls not styleUrl
})
export class AddCategoryComponent {
  category: Category = new Category();
  submitted = false;

  constructor(
    private categoryService: CategoryService,
    private router: Router,
    private toastService: ToastService
    ) {}

  ngOnInit(): void {
    
  }

  saveCategory(): void {
    this.category.name = this.category.name.trim();
    this.category.description = this.category.description.trim();
    if (this.category.name != '' && this.category.description != '') {
      this.categoryService.add(this.category).subscribe({
        next: (data) => {
          console.log('Category saved successfully!', data);
          this.showSuccessToast();
          this.router.navigate(['/acknowledgeHub/list-category']); // Adjust the route as needed
        },
        error: (errorResponse: HttpErrorResponse) => {
          if (errorResponse.status === 409) {
            console.log("Category is already exist");
          } else {
            console.log("an error is occured");
          }
        }
      });
    }
  }
  
  showSuccessToast() {
    this.toastService.showToast('Add Category  successful!', 'success');
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.submitted = false;
      this.saveCategory();
    } else {
      this.submitted = true;
      console.log('Invalid form');
    }
  }
}
