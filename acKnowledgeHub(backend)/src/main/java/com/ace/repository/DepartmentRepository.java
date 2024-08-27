package com.ace.repository;

import com.ace.entity.Department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);

    List<Department> findByCompanyId(Integer companyId);

    @Query("SELECT d FROM Department d JOIN d.company c WHERE d.name = ?1 AND c.name = ?2")
    Department findByNameAndCompany(String name, String company);

}
