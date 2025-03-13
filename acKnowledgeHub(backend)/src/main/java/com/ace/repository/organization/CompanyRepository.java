package com.ace.repository.organization;

import com.ace.entity.organization.Company;
import com.ace.utility.core.coreRepository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends BaseRepository<Company,Integer> {
    Optional<Company> findByName(String name);

    @Query("SELECT c FROM Company c ORDER BY c.name ASC")
    List<Company> findAllCompaniesOrderByName();

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) = LOWER(:name)")
    Company getCompanyByName(@Param("name") String name);
}
