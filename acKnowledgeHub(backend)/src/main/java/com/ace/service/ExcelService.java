package com.ace.service;


import com.ace.repository.*;
import com.ace.entity.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String processExcelFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Set<String> importedStaffIds = new HashSet<>();
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip the header row

                String staffId = dataFormatter.formatCellValue(row.getCell(1));
                String staffName = dataFormatter.formatCellValue(row.getCell(2));
                String staffEmail = dataFormatter.formatCellValue(row.getCell(5));
                String staffPh = dataFormatter.formatCellValue(row.getCell(6));
                String departmentName = dataFormatter.formatCellValue(row.getCell(8));
                String positionName = dataFormatter.formatCellValue(row.getCell(3));
                String companyName = dataFormatter.formatCellValue(row.getCell(4));
                String staffAddress = dataFormatter.formatCellValue(row.getCell(9));
                String staffPh2 = dataFormatter.formatCellValue(row.getCell(7));

                if (staffId == null || staffId.isEmpty()) {
                    continue; // Skip rows without a staff ID
                }
                if (staffPh2 == null || staffPh2.isEmpty()) {
                    staffPh2 = "N/A"; // Default value for secondary phone
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

                if(staff == null ){
                // Set staff details
                staff.setName(staffName);
                staff.setCompany(company);
                staff.setDepartment(department);
                staff.setPosition(position);
                staff.setCompanyStaffId(staffId);
                staff.setEmail(staffEmail);
                staffRepository.save(staff);
            }
            }

            // Update status of existing staff and users
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

            return "File processed and data saved successfully.";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file.";
        }
    }
}


