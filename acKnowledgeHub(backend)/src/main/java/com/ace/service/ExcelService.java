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
import java.util.Optional;
import java.util.Set;

@Service
public class ExcelService {


    private final StaffRepository staffRepository;
    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    public ExcelService(StaffRepository staffRepository, PositionRepository positionRepository, CompanyRepository companyRepository, DepartmentRepository departmentRepository) {
        this.staffRepository = staffRepository;
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
        this.departmentRepository = departmentRepository;
    }


    public String processExcelFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Set<String> importedStaffId = new HashSet<>();
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String staffId = dataFormatter.formatCellValue(row.getCell(1));
                String staffName = dataFormatter.formatCellValue(row.getCell(2));
                String staffEmail = dataFormatter.formatCellValue(row.getCell(5));
                String departmentName = dataFormatter.formatCellValue(row.getCell(6));
                String positionName = dataFormatter.formatCellValue(row.getCell(3));
                String companyName = dataFormatter.formatCellValue(row.getCell(4));

                if (staffId == null || staffId.isEmpty()) {
                    continue;
                }

                importedStaffId.add(staffId);


                Company company=companyRepository.findByName(companyName).stream().findFirst()
                        .orElseGet(() -> {
                            Company newCompany = new Company();
                            newCompany.setName(companyName);
                            return companyRepository.save(newCompany);
                        });


                Department department = Optional.ofNullable(departmentRepository.findByNameAndCompany(departmentName,companyName)).stream().findFirst()
                        .orElseGet(() -> {
                            Department newDepartment = new Department();
                            newDepartment.setName(departmentName);
                            newDepartment.setCompany(company);
                            return departmentRepository.save(newDepartment);
                        });

                Position position = positionRepository.findByName(positionName).stream().findFirst()
                        .orElseGet(() -> {
                            Position newPosition = new Position();
                            newPosition.setName(positionName);
                            return positionRepository.save(newPosition);
                        });

                Staff staff = Optional.ofNullable(staffRepository.findByCompanyStaffId(staffId))
                        .orElseGet(() -> new Staff());

                staff.setName(staffName);
                staff.setCompany(company);
                staff.setDepartment(department);
                staff.setPosition(position);
                staff.setCompanyStaffId(staffId);
                staff.setEmail(staffEmail);
                staffRepository.save(staff);


            }
            staffRepository.findAll().forEach(staff -> {
                if (!importedStaffId.contains(staff.getCompanyStaffId())) {
                    staff.setStatus("inactive");
                } else {
                    staff.setStatus("active");
                }
                staffRepository.save(staff);

                staffRepository.findById(staff.getId()).ifPresent(user -> {
                    if (!importedStaffId.contains(staff.getCompanyStaffId())) {
                        user.setStatus("inactive");
                    } else {
                        user.setStatus("active");
                    }
                    staffRepository.save(user);
                });

            });
            return "File processed and data saved successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file.";
        }
    }
}
