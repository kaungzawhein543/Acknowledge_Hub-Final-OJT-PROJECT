package com.ace.controller;

import com.ace.entity.Department;
import com.ace.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/sys/getAllCompany")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
    @GetMapping("/sys/company/{companyId}")
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


}
