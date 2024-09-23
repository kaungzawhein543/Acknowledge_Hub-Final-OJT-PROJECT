package com.ace.controller;

import com.ace.entity.Company;
import com.ace.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/company/sys")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public Company getCompanyById(@PathVariable int id) {
        return companyService.getCompanyById(id);
    }

    @PostMapping
    public ResponseEntity<String> createCompany(@RequestBody Company company) {
        Company existingCompany = companyService.findByName(company.getName());
        if (existingCompany == null) {
            companyService.saveCompany(company);
            return ResponseEntity.ok("Adding company is successful.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Company already exists.");
        }

    }

    @PutMapping("/{id}")
    public Company updateCompany(@PathVariable int id, @RequestBody Company company) {
        return companyService.updateCompany(id, company);
    }

    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable int id) {
        companyService.deleteCompany(id);
    }
}
