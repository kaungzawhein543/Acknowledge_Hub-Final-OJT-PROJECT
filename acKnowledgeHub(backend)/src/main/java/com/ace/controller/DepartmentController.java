package com.ace.controller;

import com.ace.entity.organization.Company;
import com.ace.entity.organization.Department;
import com.ace.service.CompanyService;
import com.ace.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/department")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final CompanyService companyService;

    public DepartmentController(DepartmentService departmentService, CompanyService companyService) {
        this.departmentService = departmentService;
        this.companyService = companyService;
    }

    @GetMapping("/sys/getAllCompany")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/all/company/{companyId}")
    public List<Department> getDepartmentsByCompany(@PathVariable Integer companyId) {
        return departmentService.getDepartmentsByCompanyId(companyId);
    }

    @GetMapping("/sys/{id}")
    public Department getDepartmentById(@PathVariable int id) {
        return departmentService.getDepartmentById(id);
    }

    @PostMapping("/sys/createDepartment")
    public ResponseEntity<String> createDepartment(@RequestBody Department department) {
        Department existingDepartment = departmentService.getDepartmentByLowerName(department.getName(),department.getCompany().getId());
        if(existingDepartment == null){
            departmentService.saveDepartment(department);
            return ResponseEntity.ok("Adding department is successful.");
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Department is already exist");
        }
    }

    @PostMapping("/allHR/createDepartment/{departmentName}/{companyName}")
    public ResponseEntity<String> createDepartmentHR(@PathVariable String departmentName, @PathVariable String companyName) {
        Company hrCompany = companyService.findByName(companyName);
        if (hrCompany != null) {
            Department existingDepartment = departmentService.getDepartmentByLowerName(departmentName, hrCompany.getId());
            if (existingDepartment == null) {
                Department department = new Department();
                department.setName(departmentName);
                department.setCompany(hrCompany);
                departmentService.saveDepartment(department);
                return ResponseEntity.ok("Adding department is successful.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Department already exists.");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("COMPANY is undefined.");
    }

    @PutMapping("/all/{id}")
    public ResponseEntity<String> updateDepartment(@PathVariable("id") Integer id, @RequestBody Department department) {
        try {
            Department existingDepartment = departmentService.getDepartmentByLowerName(department.getName(), department.getCompany().getId());

            if (existingDepartment == null) {
                departmentService.updateDepartment(id, department);
                return ResponseEntity.ok("Updating department is successful.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Department already exists");
            }
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred due to a null value.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }


}
