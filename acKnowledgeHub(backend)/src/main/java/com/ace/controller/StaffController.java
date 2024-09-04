package com.ace.controller;

import com.ace.dto.*;
import com.ace.entity.Staff;
import com.ace.service.StaffService;
import org.modelmapper.ModelMapper;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/staff")
public class StaffController {

    private  final StaffService staffService;
    private final ModelMapper mapper;
    private final PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler;

    public StaffController(StaffService staffService, ModelMapper mapper,PagedResourcesAssembler<StaffGroupDTO> pagedResourcesAssembler) {
        this.staffService = staffService;
        this.mapper = mapper;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping("/group-staff")
    public List<StaffGroupDTO> getStaffListByDepartmentId(){
        List<StaffGroupDTO> staffList = staffService.getStaffListForGroup();
        return staffList;
    }

    @GetMapping("/noted-list/{id}")
    public List<NotedResponseDTO> getNotedStaff(@PathVariable("id") Integer announcementId){
        List<NotedResponseDTO> staffList =staffService.getNotedStaffList(announcementId);
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


    @GetMapping("/not-noted-list/{id}")
    public List<UnNotedResponseDTO> getUnNotedStaff(@PathVariable("id") Integer announcementId){
        List<UnNotedResponseDTO> staffList = staffService.getUnNotedStaffList(announcementId);
        return staffList;
    }
}
