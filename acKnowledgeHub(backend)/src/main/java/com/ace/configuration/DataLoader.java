package com.ace.configuration;

import com.ace.entity.*;
import com.ace.enums.Role;
import com.ace.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class DataLoader {

    private final StaffRepository staffRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    public DataLoader( StaffRepository staffRepository,
                      CompanyRepository companyRepository, PositionRepository positionRepository,
                      DepartmentRepository departmentRepository) {
        this.staffRepository = staffRepository;
        this.companyRepository = companyRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            if (staffRepository.findByEmail("admin@example.com") == null) {

                Company company = companyRepository.findByName("Default Company").stream().findFirst()
                        .orElseGet(() -> {
                            Company newCompany = new Company();
                            newCompany.setName("ACE PLUS");
                            return companyRepository.save(newCompany);
                        });

                Department department = departmentRepository.findByName("Default Department").stream().findFirst()
                        .orElseGet(() -> {
                            Department newDepartment = new Department();
                            newDepartment.setName("ERP");
                            newDepartment.setCompany(company);
                            return departmentRepository.save(newDepartment);
                        });

                Position position = positionRepository.findByName("Default Position").stream().findFirst()
                        .orElseGet(() -> {
                            Position newPosition = new Position();
                            newPosition.setName("Manager");
                            return positionRepository.save(newPosition);
                        });

                Staff adminStaff = new Staff();
                adminStaff.setName("Admin");
                adminStaff.setCompanyStaffId("ADMIN001");
                adminStaff.setEmail("admin@example.com");
                adminStaff.setCreatedAt(new Date());
                adminStaff.setStatus("active");
                adminStaff.setCompany(company);
                adminStaff.setDepartment(department);
                adminStaff.setPosition(position);
                adminStaff.setRole(Role.ADMIN);
                staffRepository.save(adminStaff);
            }
        };
    }
}




