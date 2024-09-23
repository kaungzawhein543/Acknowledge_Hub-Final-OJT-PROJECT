//package com.ace.controller;
//
//import com.ace.entity.Staff;
//import com.ace.service.StaffService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/selection")
//public class SelectionController {
//    private final StaffService staffService;
//
//    public SelectionController(StaffService staffService) {
//        this.staffService = staffService;
//    }
//
//    @GetMapping("/users/{staffId}")
//    public Staff getUserByStaffId(@PathVariable String staffId) {
//        return staffService.findByStaffId(staffId);
//    }
//}
