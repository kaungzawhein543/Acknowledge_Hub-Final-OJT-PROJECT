package com.ace.service;

import com.ace.entity.Group;
import com.ace.repository.DepartmentRepository;
import com.ace.entity.Department;
import com.ace.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;

    public DepartmentService(DepartmentRepository departmentRepository, GroupRepository groupRepository) {
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAllDepartmentsOrderByName();
    }
    public List<Department> getDepartmentsByCompanyId(Integer companyId) {
        return departmentRepository.findByCompanyId(companyId);
    }

    public Department getDepartmentByLowerName(String name,Integer companyId){
        return departmentRepository.getDepartmentByLowerName(name,companyId);
    }


    public Department getDepartmentById(int id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department saveDepartment(Department department) {
        Group group = groupRepository.findByName(department.getName());
        if(group == null){
           // System.out.println("here is comapy name in service"+department.getCompany().getName());
            Group group1 = new Group();
            group1.setName(department.getName()+"("+department.getCompany().getName()+")");
            groupRepository.save(group1);
        }
        return departmentRepository.save(department);
    }


    public Department updateDepartment(int id, Department updatedDepartment) {
        Optional<Department> existingDepartment = departmentRepository.findById(id);
        if (existingDepartment.isPresent()) {
            Department department = existingDepartment.get();
            department.setName(updatedDepartment.getName());
            department.setCompany(updatedDepartment.getCompany());
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
