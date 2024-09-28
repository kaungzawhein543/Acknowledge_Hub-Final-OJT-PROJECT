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

    public ExcelService(StaffRepository staffRepository, PositionRepository positionRepository,
                        CompanyRepository companyRepository, DepartmentRepository departmentRepository,
                        GroupRepository groupRepository, EmailService emailService) {
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
                String telegramName = dataFormatter.formatCellValue(row.getCell(6));

                if (staffId == null || staffId.isEmpty()) continue; // Skip rows without a staff ID
                importedStaffIds.add(staffId);

                Staff staff = createOrUpdateStaff(staffId, staffName, staffEmail, positionName, companyName, departmentName,telegramName);

                addStaffToGroups(staff, companyName, departmentName);

                if (!overrideExisting && staffRepository.findByCompanyStaffId(staffId) == null) {
                    // If staff is new, send email
                    if (staffEmail != null && !staffEmail.trim().isEmpty()) {
                        emailsToSend.add(staffEmail.trim());
                    }
                }
            }

            sendEmailsAsync(emailsToSend); // Send emails asynchronously
            if (overrideExisting) {
                updateStaffStatusAsync(importedStaffIds); // Update staff status asynchronously
            }

            return "Staff added/updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing file.";
        }
    }

    private Staff createOrUpdateStaff(String staffId, String staffName, String staffEmail,
                                      String positionName, String companyName, String departmentName,String telegramName) {
        Company company = findOrCreateCompany(companyName);
        Department department = findOrCreateDepartment(departmentName, company);
        Position position = findOrCreatePosition(positionName);

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
        staff.setTelegramName(telegramName);
        return staffRepository.save(staff);
    }

    private Company findOrCreateCompany(String companyName) {
        return companyRepository.findByName(companyName).stream().findFirst()
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    return companyRepository.save(newCompany);
                });
    }

    private Department findOrCreateDepartment(String departmentName, Company company) {
        Department department = departmentRepository.findByNameAndCompany(departmentName, company.getName());
        if (department == null) {
            department = new Department();
            department.setName(departmentName);
            department.setCompany(company);
            return departmentRepository.save(department);
        }
        return department;
    }

    private Position findOrCreatePosition(String positionName) {
        return positionRepository.findByName(positionName).stream().findFirst()
                .orElseGet(() -> {
                    Position newPosition = new Position();
                    newPosition.setName(positionName);
                    return positionRepository.save(newPosition);
                });
    }

    @Async
    private void addStaffToGroups(Staff staff, String companyName, String departmentName) {
        addToGroup(staff, companyName);
        addToGroup(staff, departmentName + " (" + companyName + ")");
        addToGroup(staff, "Global Group");
    }

    @Async
    private void addToGroup(Staff staff, String groupName) {
        Group group = groupRepository.findByName(groupName);
        if (group == null) {
            group = new Group();
            group.setName(groupName);
            group.setStaff(new ArrayList<>(Collections.singletonList(staff)));
            groupRepository.save(group);
        } else if (groupRepository.hasStaffInGroup(staff, group) == 0) {
            group.getStaff().add(staff);
            groupRepository.save(group);
        }
    }

    @Async
    public CompletableFuture<Void> updateStaffStatusAsync(Set<String> importedStaffIds) {
        return CompletableFuture.runAsync(() -> {
            staffRepository.findAll().forEach(staff -> {

                if (staff.getCompanyStaffId().equals("ADMIN001") || staff.getPosition().equals("Human Resource(Main)")) {
                    // Always keep ADMIN001 active
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
