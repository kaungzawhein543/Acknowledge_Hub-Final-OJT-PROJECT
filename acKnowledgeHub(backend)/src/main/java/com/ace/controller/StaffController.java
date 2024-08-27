package com.ace.controller;

import com.ace.dto.NotedResponseDTO;
import com.ace.dto.StaffGroupDTO;
import com.ace.dto.UnNotedResponseDTO;
import com.ace.service.StaffService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/staff")
public class StaffController {

    private  final StaffService staffService;
    private final ModelMapper mapper;
    public StaffController(StaffService staffService, ModelMapper mapper) {
        this.staffService = staffService;
        this.mapper = mapper;
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

    @GetMapping("/not-noted-list/{id}")
    public List<UnNotedResponseDTO> getUnNotedStaff(@PathVariable("id") Integer announcementId){
        List<UnNotedResponseDTO> staffList = staffService.getUnNotedStaffList(announcementId);
        return staffList;
    }
}
