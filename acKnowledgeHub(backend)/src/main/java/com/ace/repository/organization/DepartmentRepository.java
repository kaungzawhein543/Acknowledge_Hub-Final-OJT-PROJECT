package com.ace.repository.organization;

import com.ace.entity.organization.Department;

import com.ace.utility.core.coreRepository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Integer> {
    Optional<Department> findByName(String name);

    List<Department> findByCompanyId(Integer companyId);

    @Query("SELECT d FROM Department d ORDER BY d.name ASC")
    List<Department> findAllDepartmentsOrderByName();

    @Query("SELECT d FROM Department d JOIN d.company c WHERE d.name = ?1 AND c.name = ?2")
    Department findByNameAndCompany(String name, String company);

    @Query("select d from Department d where Lower(d.name)=Lower(:name) and d.company.id = :companyId")
    Department getDepartmentByLowerName(@Param("name")String name,@Param("companyId")Integer companyId);

}
