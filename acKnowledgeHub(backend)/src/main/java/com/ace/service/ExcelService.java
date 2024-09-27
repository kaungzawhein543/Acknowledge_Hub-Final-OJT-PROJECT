package com.ace.service;

import com.ace.entity.*;
import com.ace.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ExcelService {

    @Value("${default.photo.path}")
    private String defaultPath;

    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final GroupRepository groupRepository;
    private final EmailService emailService;

    public ExcelService(StaffRepository staffRepository, PositionRepository positionRepository, CompanyRepository companyRepository, DepartmentRepository departmentRepository, GroupRepository groupRepository, EmailService emailService) {
        this.staffRepository = staffRepository;
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
        this.departmentRepository = departmentRepository;
        this.groupRepository = groupRepository;
        this.emailService = emailService;
    }

    @Transactional
    public String processExcelFile(MultipartFile file, boolean overrideExisting) throws IOException {
        Set<String> emailsToSend = new HashSet<>();
        Set<String> importedStaffIds = new HashSet<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                String staffId = dataFormatter.formatCellValue(row.getCell(0));
                String staffName = dataFormatter.formatCellValue(row.getCell(1));
                String staffEmail = dataFormatter.formatCellValue(row.getCell(2));
                String positionName = dataFormatter.formatCellValue(row.getCell(3));
                String departmentName = dataFormatter.formatCellValue(row.getCell(4));
                String companyName = dataFormatter.formatCellValue(row.getCell(5));
                String groupName = dataFormatter.formatCellValue(row.getCell(6)); // Added group name

                if (staffId == null || staffId.isEmpty()) {
                    continue; // Skip rows without a staff ID
                }
                importedStaffIds.add(staffId);

                // Choose between overriding existing staff or adding only new staff
                if (overrideExisting) {
                    overrideAddStaffs(staffId, staffName, staffEmail, positionName, companyName, departmentName, groupName);
                } else {
                    addOnlyStaffs(staffId, staffName, staffEmail, positionName, companyName, departmentName, groupName, emailsToSend);
                }
            }

            // Send emails in the background
            sendEmailsAsync(emailsToSend); // Don't wait for this to finish

            // If overriding existing staff, update their status without waiting
            if (overrideExisting) {
                updateStaffStatusAsync(importedStaffIds); // Also don't wait for this to finish
            }

            // Return immediately after adding/updating staff
            return "Staff added/updated successfully.";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file.";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing file", e);
        }
    }

    private void overrideAddStaffs(String staffId, String staffName, String staffEmail, String positionName, String companyName, String departmentName, String groupName) {
        // Your existing logic for adding/updating staff...
        Company company = companyRepository.findByName(companyName).stream().findFirst()
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    return companyRepository.save(newCompany);
                });

        Department department = departmentRepository.findByNameAndCompany(departmentName, companyName);
        if (department == null) {
            department = new Department();
            department.setName(departmentName);
            department.setCompany(company);
            department = departmentRepository.save(department);
        }

        Position position = positionRepository.findByName(positionName).stream().findFirst()
                .orElseGet(() -> {
                    Position newPosition = new Position();
                    newPosition.setName(positionName);
                    return positionRepository.save(newPosition);
                });

        Staff staff = staffRepository.findByCompanyStaffId(staffId);
        if (staff == null) {
            staff = new Staff();
            staff.setCompanyStaffId(staffId);
        }

        staff.setName(staffName);
        staff.setCompany(company);
        staff.setDepartment(department);
        staff.setPosition(position);
        staff.setEmail(staffEmail);
        staff.setPhotoPath(defaultPath);

        // Associate staff with a group
        Group group = groupRepository.findByName(groupName);

        if (group==null) {
            group = new Group();
            group.setName(groupName);
            group = groupRepository.save(group);
        }

        // Ensure staff has a list of groups initialized, then add the group
        if (staff.getGroups() == null) {
            staff.setGroups(new ArrayList<>()); // Initialize the list if it's null
        }
        staff.getGroups().add(group); // Add the group to the list

        staffRepository.save(staff);
    }

    private void addOnlyStaffs(String staffId, String staffName, String staffEmail, String positionName, String companyName, String departmentName, String groupName, Set<String> emailsToSend) {
        Staff existingStaff = staffRepository.findByCompanyStaffId(staffId);
        if (existingStaff == null) {
            overrideAddStaffs(staffId, staffName, staffEmail, positionName, companyName, departmentName, groupName);
            if (staffEmail != null && !staffEmail.trim().isEmpty()) {
                emailsToSend.add(staffEmail.trim());
            }
        }
    }

    @Async
    public CompletableFuture<Void> updateStaffStatusAsync(Set<String> importedStaffIds) {
        return CompletableFuture.runAsync(() -> {
            staffRepository.findAll().forEach(staff -> {
                if (staff.getCompanyStaffId().equals("ADMIN001")) {
                    staff.setStatus("active");
                } else {
                    if (!importedStaffIds.contains(staff.getCompanyStaffId())) {
                        staff.setStatus("inactive");
                    } else {
                        staff.setStatus("active");
                    }
                }
                staffRepository.save(staff);
            });
        });
    }

    @Async
    public CompletableFuture<Void> sendEmailsAsync(Set<String> emailsToSend) {
        return CompletableFuture.runAsync(() -> {
            for (String email : emailsToSend) {
                try {
                    emailService.sendTelegramChannelInvitation(email);
                    System.out.println("Email sent successfully to: " + email);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error sending email to: " + email);
                }
            }
        });
    }
}
