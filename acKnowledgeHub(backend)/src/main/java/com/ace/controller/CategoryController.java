package com.ace.controller;

import com.ace.entity.Category;
import com.ace.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping(value = "/sys/save")
    public Category save(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "description") String description) throws IOException {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setCreatedAt(LocalDate.now());
        return service.save(category);
    }



    @PutMapping(value = "/sys/update/{id}")
    public Category update(@PathVariable("id") int id,
                           @RequestParam("name") String name,
                           @RequestParam("description") String description) throws IOException {
        Category updated = new Category();
        updated.setId(id);
        updated.setName(name);
        updated.setDescription(description);
        return service.update(id, updated);
    }

    @DeleteMapping("/sys/deleteCategory/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id){
        Optional<Category> resultCategory = service.showById(id);
        if(resultCategory.isPresent()){
            service.deleteCategory(id);
            return ResponseEntity.ok("Delete Category Successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category");
    }

    @PutMapping("/sys/softDeleteCategory/{id}")
    public ResponseEntity<Void> deleteSoftly(@PathVariable Integer id) {
        Optional<Category> resultCategory = service.showById(id);
        if (resultCategory.isPresent()) {
            service.deleteSoftly(id);
            return ResponseEntity.ok().build();  // No body
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();  // No body
    }


    @GetMapping("/all/allcategories")
    public ResponseEntity<List<Category>> getAllParentCategory() {
        List<Category> categories = service.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/all/category/{id}")
    public ResponseEntity<Category> showById(@PathVariable int id) {
        Optional<Category> category = service.showById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
