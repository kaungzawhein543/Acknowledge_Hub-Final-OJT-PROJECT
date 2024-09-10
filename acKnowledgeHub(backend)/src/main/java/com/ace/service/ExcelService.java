package com.ace.service;


import com.ace.repository.*;
import com.ace.entity.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ExcelService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
    private final JavaMailSender javaMailSender;
    private final EmailService emailService;

    public ExcelService(JavaMailSender javaMailSender, EmailService emailService) {
        this.javaMailSender = javaMailSender;
        this.emailService = emailService;
    }

    public String processExcelFile(MultipartFile file) throws IOException {
        Set<String> emailsToSend = new HashSet<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Set<String> importedStaffIds = new HashSet<>();
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip the header row

                String staffId = dataFormatter.formatCellValue(row.getCell(0));
                String staffName = dataFormatter.formatCellValue(row.getCell(1));
                String staffEmail = dataFormatter.formatCellValue(row.getCell(2));
                if (staffEmail != null && !staffEmail.isEmpty() && staffRepository.findByEmail(staffEmail.trim()) == null) {
                    emailsToSend.add(staffEmail.trim());
                }

                String positionName = dataFormatter.formatCellValue(row.getCell(3));
                String companyName = dataFormatter.formatCellValue(row.getCell(4));
                String departmentName = dataFormatter.formatCellValue(row.getCell(5));

                if (staffId == null || staffId.isEmpty()) {
                    continue; // Skip rows without a staff ID
                }
                importedStaffIds.add(staffId);

                // Retrieve or create the company
                Company company = companyRepository.findByName(companyName).stream().findFirst()
                        .orElseGet(() -> {
                            Company newCompany = new Company();
                            newCompany.setName(companyName);
                            return companyRepository.save(newCompany);
                        });

                // Retrieve the department by name and company
                Department department = departmentRepository.findByNameAndCompany(departmentName, companyName);

                if (department == null) {
                    // Create a new department if none was found
                    department = new Department();
                    department.setName(departmentName);
                    department.setCompany(company);

                    department = departmentRepository.save(department);
                }

                // Retrieve or create the position
                Position position = positionRepository.findByName(positionName).stream().findFirst()
                        .orElseGet(() -> {
                            Position newPosition = new Position();
                            newPosition.setName(positionName);
                            return positionRepository.save(newPosition);
                        });

                // Retrieve or create the staff
                Staff staff = staffRepository.findByCompanyStaffId(staffId);

                if (staff == null) {
                    staff = new Staff();
                    staff.setCompanyStaffId(staffId);
                }

                // Update staff details
                staff.setName(staffName);
                staff.setCompany(company);
                staff.setDepartment(department);
                staff.setPosition(position);
                staff.setEmail(staffEmail);

                // Save or update the staff
                staffRepository.save(staff);
            }

            // Update status of existing staff
            staffRepository.findAll().forEach(staff -> {
                if (staff.getCompanyStaffId().equals("ADMIN001")) {
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
            sendEmails(emailsToSend);
            return "File processed and data saved successfully.";


        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file.";
        }
    }
    private void sendEmails(Set<String> emailsToSend) {
        for (String email : emailsToSend) {
            try {
                emailService.sendTelegramChannelInvitation(email);
                System.out.println("Email sent successfully to: " + email);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error sending email to: " + email);
            }
        }
    }
}


