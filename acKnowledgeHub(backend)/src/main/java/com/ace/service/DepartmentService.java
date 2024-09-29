package com.ace.service;

import com.ace.entity.Group;
import com.ace.repository.DepartmentRepository;
import com.ace.entity.Department;
import com.ace.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
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

    public Department getDepartmentByLowerName(String name, Integer companyId) {
        return departmentRepository.getDepartmentByLowerName(name, companyId);
    }


    public Department getDepartmentById(int id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department saveDepartment(Department department) {
        Group group = groupRepository.findByName(department.getName());
        if (group == null) {
            // System.out.println("here is comapy name in service"+department.getCompany().getName());
            Group group1 = new Group();
            group1.setName(department.getName() + "(" + department.getCompany().getName() + ")");
            groupRepository.save(group1);
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(int id, Department updatedDepartment) {
        // Step 1: Retrieve the Department by ID
        Optional<Department> existingDepartmentOpt = departmentRepository.findById(id);

        if (!existingDepartmentOpt.isPresent()) {
            log.info("Department with id " + id + " not found.");
            throw new RuntimeException("Department not found");
        }
        Department existingDepartment = existingDepartmentOpt.get();
        String currentGroupName = (existingDepartment.getName() + " (" + existingDepartment.getCompany().getName() + ")").trim();
        Group departmentGroup = groupRepository.findByName(currentGroupName);
        String updatedGroupName = (updatedDepartment.getName() + " (" + updatedDepartment.getCompany().getName() + ")").trim();
        departmentGroup.setName(updatedGroupName);
        groupRepository.save(departmentGroup);  // Save the modified group

        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setCompany(updatedDepartment.getCompany());

        Department savedDepartment = departmentRepository.save(existingDepartment);

        return savedDepartment;
    }

}
