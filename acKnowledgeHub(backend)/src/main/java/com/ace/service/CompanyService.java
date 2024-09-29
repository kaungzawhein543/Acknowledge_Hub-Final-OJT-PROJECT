package com.ace.service;

import com.ace.entity.Group;
import com.ace.repository.CompanyRepository;
import com.ace.entity.Company;
import com.ace.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return companyRepository.findAllCompaniesOrderByName();
    }

//    public Optional<Company> findById(int id ){
//        return companyRepository.findById(id);
//    }

    public Company getCompanyById(int id) {
        return companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public Company saveCompany(Company company) {
        Group group = groupRepository.findByName(company.getName());
        if (group == null) {
            Group group1 = new Group();
            group1.setName(company.getName());
            groupRepository.save(group1);
        }

        return companyRepository.save(company);
    }

    @Transactional
    public Company updateCompany(int id, String updatedCompany) {
        Optional<Company> existingCompany = companyRepository.findById(id);
        System.out.println(existingCompany);
        String companyName = existingCompany.get().getName();
        List<Group> groupList = groupRepository.getGroupsByName(companyName);
        System.out.println(groupList);
        for (Group group : groupList) {
            String currentGroupName = group.getName();
            String updatedGroupName = currentGroupName.replace(companyName, updatedCompany);
            group.setName(updatedGroupName);
            groupRepository.save(group);
        }

        if (existingCompany.isPresent()) {
            Company company = existingCompany.get();
            company.setName(updatedCompany);
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

    public Company findByName(String name) {
        return companyRepository.getCompanyByName(name);
    }

}
