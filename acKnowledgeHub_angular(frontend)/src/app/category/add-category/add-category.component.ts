import { Component, OnInit } from '@angular/core';
import { Category } from '../../models/category';
import { CategoryService } from '../../services/category.service';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-add-category',
  templateUrl: './add-category.component.html',
  styleUrls: ['./add-category.component.css'] // Note: It's styleUrls not styleUrl
})
export class AddCategoryComponent implements OnInit {
  category: Category = new Category();
  submitted = false;

  constructor(
    private categoryService: CategoryService, 
    private router: Router,
    ) {}

  ngOnInit(): void {
    
  }

  saveCategory(): void {

      this.categoryService.add(this.category).subscribe(
        data => {
          console.log('Category saved successfully!', data);
          this.router.navigate(['/list-category']); // Adjust the route as needed
        },
        error => {
          console.error('There was an error saving the category!', error);
        }
      );
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
