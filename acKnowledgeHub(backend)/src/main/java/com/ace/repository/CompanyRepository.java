package com.ace.repository;

import com.ace.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Integer> {
    Optional <Company> findByName(String name);

    @Query("SELECT c FROM Company c ORDER BY c.name ASC")
    List<Company> findAllCompaniesOrderByName();

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) = LOWER(:name)")
    Company getCompanyByName(@Param("name") String name);
}
