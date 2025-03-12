package com.ace.test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ace.entity.Category;
import com.ace.repository.CategoryRepository;
import com.ace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testSave() throws IOException {
        // Arrange
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");

        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category savedCategory = categoryService.save(category);

        // Assert
        assertNotNull(savedCategory);
        assertEquals("Test Category", savedCategory.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testUpdate() {

        Category existingCategory = new Category();
        existingCategory.setId(1);
        existingCategory.setName("Old Name");
        existingCategory.setDescription("Old Description");

        Category updateCategory = new Category();
        updateCategory.setName("New Name");
        updateCategory.setDescription("New Description");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category updatedCategory = categoryService.update(1, updateCategory);

        assertEquals("New Name", updatedCategory.getName());
        assertEquals("New Description", updatedCategory.getDescription());
        verify(categoryRepository).findById(1);
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void update_nonExistingCategory_shouldThrowException() {
        int categoryId = 1;
        Category updateCategory = new Category();
        updateCategory.setName("New Name");
        updateCategory.setDescription("New Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.update(categoryId, updateCategory);
        });

        assertEquals("Category with ID " + categoryId + " not found.", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testGetAllCategories() {
        Category c1 = new Category();
        c1.setId(1);
        c1.setName("Name One");
        c1.setDescription("Description 1");
        Category c2 = new Category();
        c2.setId(2);
        c2.setName("Name Two");
        c2.setDescription("Description 2");
        List<Category> categoryList = Arrays.asList(c1, c2);

        when(categoryRepository.findAllActiveCategories()).thenReturn(categoryList);

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Name One", result.get(0).getName());
        assertEquals("Name Two", result.get(1).getName());
        verify(categoryRepository).findAllActiveCategories();
    }

    @Test
    void testShowById() {
        int categoryId = 1;
        Category c1 = new Category();
        c1.setId(categoryId);
        c1.setName("Name One");
        c1.setDescription("Description 1");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(c1));

        Optional<Category> result = categoryService.showById(categoryId);

        assertTrue(result.isPresent());
        assertEquals("Name One", result.get().getName());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void testSoftDeleteCategory() {
        Integer categoryId = 1;
        categoryService.deleteSoftly(categoryId);
        verify(categoryRepository).softDeleteCategory(categoryId);
    }
}
