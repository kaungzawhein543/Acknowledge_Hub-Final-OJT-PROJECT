package com.ace.controller;

import com.ace.dto.*;
import com.ace.entity.Company;
import com.ace.entity.Department;
import com.ace.entity.Position;
import com.ace.entity.Staff;
import com.ace.service.CompanyService;
import com.ace.service.DepartmentService;
import com.ace.service.PositionService;
import com.ace.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/staff")
public class StaffController {

    private final StaffService staffService;
    private final ModelMapper mapper;
    private final CompanyService companyService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
private final PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler;
    public StaffController(StaffService staffService, ModelMapper mapper, CompanyService companyService, DepartmentService departmentService, PositionService positionService,PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler) {
        this.staffService = staffService;
        this.mapper = mapper;
        this.companyService = companyService;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping("/list")
    public List<StaffResponseDTO> getStaffList(){
        return staffService.getStaffList();
    }

    @GetMapping("/active-list")
    public List<ActiveStaffResponseDTO> getActiveStaffList(){
        return staffService.getActiveStaffList();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStaff(@RequestBody StaffRequestDTO staffRequestDTO) {

        try {
            Staff staff = new Staff();
            Optional<Company> company = companyService.findById(staffRequestDTO.getCompanyId());
            staff.setCompany(company.get());
            Optional<Department> department = departmentService.findById(staffRequestDTO.getDepartmentId());
            staff.setDepartment(department.get());
            Optional<Position> position = positionService.findById(staffRequestDTO.getPositionId());
            staff.setPosition(position.get());
            staff.setRole(staffRequestDTO.getRole());
            staff.setCompanyStaffId(staffRequestDTO.getCompanyStaffId());
            staff.setName(staffRequestDTO.getName());
            staff.setEmail(staffRequestDTO.getEmail());
            staffService.addStaff(staff);
            return ResponseEntity.ok("Adding is successful.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding staff: " + e.getMessage());
        }
    
    @GetMapping("/group-staff")
    public List<StaffGroupDTO> getStaffListByDepartmentId() {
        List<StaffGroupDTO> staffList = staffService.getStaffListForGroup();
        return staffList;
    }

    @GetMapping("/noted-list/{id}")
    public List<NotedResponseDTO> getNotedStaff(@PathVariable("id") Integer announcementId) {
        List<NotedResponseDTO> staffList = staffService.getNotedStaffList(announcementId);
        return staffList;
    }

    @PostMapping("/not-noted-list/{id}")
    public List<UnNotedResponseDTO> getUnNotedStaff(@PathVariable("id") Integer announcementId, @RequestParam("groupStatus") byte groupStatus) {
        List<UnNotedResponseDTO> staffList = new ArrayList<UnNotedResponseDTO>();
        if (groupStatus == 1) {
            staffList = staffService.getUnNotedStaffListWithGroup(announcementId);
        } else if (groupStatus == 0) {
            staffList = staffService.getUnNotedStaffList(announcementId);
        }
        return staffList;
        }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedResponse<StaffDTO>> getStaffs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String searchTerm) {
        Page<StaffDTO> staffPage;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            staffPage = staffService.searchStaffs(searchTerm, page, size);
        }else{
            staffPage = staffService.getStaffs(page, size);
        }

       PaginatedResponse<StaffDTO> response = new PaginatedResponse<>(staffPage);
        return ResponseEntity.ok(response);
    }

}
