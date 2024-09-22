package com.ace.service;

import com.ace.entity.Group;
import com.ace.repository.CompanyRepository;
import com.ace.entity.Company;
import com.ace.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final GroupRepository groupRepository;

    public CompanyService(CompanyRepository companyRepository, GroupRepository groupRepository) {
        this.companyRepository = companyRepository;
        this.groupRepository = groupRepository;
    }

    public List<Company> getAllCompanies() {

        return companyRepository.findAll();
    }

//    public Optional<Company> findById(int id ){
//        return companyRepository.findById(id);
//    }

    public Company getCompanyById(int id) {
        return companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public Company saveCompany(Company company) {
        Group group = groupRepository.findByName(company.getName());
        if(group == null){
            Group group1 = new Group();
            group1.setName(company.getName());
            groupRepository.save(group1);
        }

        return companyRepository.save(company);
    }

    public Company updateCompany(int id, Company updatedCompany) {
        Optional<Company> existingCompany = companyRepository.findById(id);
        if (existingCompany.isPresent()) {
            Company company = existingCompany.get();
            company.setName(updatedCompany.getName());
            return companyRepository.save(company);
        } else {
            throw new RuntimeException("Company not found");
        }
    }

    public void deleteCompany(int id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        } else {
            throw new RuntimeException("Company not found");
        }
    }
}
