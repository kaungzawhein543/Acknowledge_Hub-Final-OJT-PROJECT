package com.ace.service;

import com.ace.repository.DepartmentRepository;
import com.ace.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    public List<Department> getDepartmentsByCompanyId(Integer companyId) {
        return departmentRepository.findByCompanyId(companyId);
    }


    public Department getDepartmentById(int id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public Department updateDepartment(int id, Department updatedDepartment) {
        Optional<Department> existingDepartment = departmentRepository.findById(id);
        if (existingDepartment.isPresent()) {
            Department department = existingDepartment.get();
            department.setName(updatedDepartment.getName());
            department.setCompany(updatedDepartment.getCompany());
            department.setAnnouncement(updatedDepartment.getAnnouncement());
            return departmentRepository.save(department);
        } else {
            throw new RuntimeException("Department not found");
        }
    }

    public void deleteDepartment(int id) {
        if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Department not found");
        }
    }
}
