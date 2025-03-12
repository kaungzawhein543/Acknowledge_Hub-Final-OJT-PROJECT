package com.ace.service;

import com.ace.entity.Category;
import com.ace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Category save(Category category) throws IOException {
        return repository.save(category);
    }

    public Category update(int id, Category update) {
        Optional<Category> categoryOptional = repository.findById(id);
        if (!categoryOptional.isPresent()) {
            throw new IllegalArgumentException("Category with ID " + id + " not found.");
        }
        Category category = categoryOptional.get();
        category.setName(update.getName());
        category.setDescription(update.getDescription());
        return repository.save(category);
    }

    public List<Category> getAllCategories() {
        return repository.findAllActiveCategories();
    }

    public Optional<Category> showById(int id) {
        return repository.findById(id);
    }

    //Delete Category
    public void deleteCategory(Integer id){
        repository.deleteById(id);
    }

    //SoftDelete Category
    public void deleteSoftly(Integer id){
        repository.softDeleteCategory(id);
    }

    public Category findByLowerName(String name){
        return repository.findByLowerName(name);
    }

}
