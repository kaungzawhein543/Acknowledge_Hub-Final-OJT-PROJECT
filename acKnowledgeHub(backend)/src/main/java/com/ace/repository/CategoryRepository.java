package com.ace.repository;

import com.ace.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.status = 'inactive' WHERE c.id = :id")
    void softDeleteCategory(@Param("id") Integer id);

    @Query("SELECT c FROM Category c WHERE c.status = 'active'")
    List<Category> findAllActiveCategories();

}
