package com.ace.test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ace.entity.Category;
import com.ace.repository.CategoryRepository;
import com.ace.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;

    @Test
    public void testSave() throws IOException {
        // Arrange
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");

        when(repository.save(category)).thenReturn(category);

        // Act
        Category savedCategory = service.save(category);

        // Assert
        assertNotNull(savedCategory);
        assertEquals("Test Category", category.getName());
        verify(repository, times(1)).save(category);
    }
}

