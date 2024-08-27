import { Component, OnInit } from '@angular/core';
import { Category } from '../../models/category';
import { CategoryService } from '../../services/category.service';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';

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
    private router: Router
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

  saveCategory(): void {
    this.categoryService.update(this.id, this.category).subscribe({
      next: (updatedCategory) => {
        console.log('Category updated successfully:', updatedCategory);
        alert('Category updated successfully!');
        this.router.navigate(['/list-category']); // Redirect to the category list after update
      },
      error: (err) => {
        console.error('Error updating category:', err);
      }
    });
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
