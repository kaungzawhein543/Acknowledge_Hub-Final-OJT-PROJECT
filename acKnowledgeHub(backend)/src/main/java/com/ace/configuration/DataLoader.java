package com.ace.configuration;

import com.ace.entity.*;
import com.ace.enums.Role;
import com.ace.repository.*;
import org.springframework.beans.factory.annotation.Value;
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
    private final GroupRepository groupRepository; // Assuming you have a GroupRepository

    @Value("${default.photo.path}")
    private String DEFAULT_PHOTO_PATH;

    public DataLoader(StaffRepository staffRepository,
                      CompanyRepository companyRepository,
                      PositionRepository positionRepository,
                      DepartmentRepository departmentRepository,
                      GroupRepository groupRepository) { // Add GroupRepository to constructor
        this.staffRepository = staffRepository;
        this.companyRepository = companyRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository; // Initialize the GroupRepository
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // Create Admin Staff if not exists
            if (staffRepository.findByEmail("admin@example.com") == null) {
                createAdminStaff();
            }

            // Create a default group that doesn't belong to any company
            createIndependentGroup();
        };
    }

    private void createAdminStaff() {
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
        adminStaff.setPhotoPath(DEFAULT_PHOTO_PATH);
        staffRepository.save(adminStaff);

        Position position1 = new Position();
        position1.setName("Human Resource(Main)");
        positionRepository.save(position1);
    }

    private void createIndependentGroup() {
        // Check if the group already exists
        if (groupRepository.findByName("Global Group") == null) {
            Group newGroup = new Group();
            newGroup.setName("Global Group"); // Set the name
            newGroup.setCreatedAt(new Date()); // Set the created_at date
            newGroup.setStatus("active"); // Set default status to active
            groupRepository.save(newGroup); // Save the group
        }
    }
}
