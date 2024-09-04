//package com.ace.controller;
//
//
//import com.ace.entity.ReqAnnouncement;
//import com.ace.service.ReqAnnouncementService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.List;
//
//@RestController
//@Slf4j
//@RequestMapping(value = "api/v1/reqAnnouncement")
//public class ReqAnnouncementController {
//
//    private final ReqAnnouncementService reqAnnouncementService;
//
//    public ReqAnnouncementController(ReqAnnouncementService reqAnnouncementService) {
//        this.reqAnnouncementService = reqAnnouncementService;
//    }
//
//    @PostMapping("/save")
//    public ResponseEntity<ReqAnnouncement> saveAnnouncement(@RequestBody ReqAnnouncement reqAnnouncement){
//        ReqAnnouncement createdAnnouncement = reqAnnouncementService.createReqAnnouncement(reqAnnouncement);
//        return ResponseEntity.created(UriComponentsBuilder.fromPath("/announcements/{id}").buildAndExpand(createdAnnouncement.getId()).toUri())
//                .body(createdAnnouncement);
//    }
//
//    // READ (ALL)
//    @GetMapping
//    public ResponseEntity<List<ReqAnnouncement>> getAllAnnouncements(){
//        List<ReqAnnouncement> announcements = reqAnnouncementService.getAllReqAnnouncement();
//        return ResponseEntity.ok(announcements);
//    }
//
//    // READ (BY ID)
//    @GetMapping("/{id}")
//    public ResponseEntity<ReqAnnouncement> getAnnouncementById(@PathVariable Integer id){
//        ReqAnnouncement announcement = reqAnnouncementService.getById(id);
//        if (announcement != null) {
//            return ResponseEntity.ok(announcement);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
////    // UPDATE
////    @PutMapping("/{id}")
////    public ResponseEntity<ReqAnnouncement> updateAnnouncement(@PathVariable Integer id, @RequestBody ReqAnnouncement reqAnnouncement){
////        ReqAnnouncement updatedAnnouncement = reqAnnouncementService.editReqAnnouncement(id, reqAnnouncement);
////        if (updatedAnnouncement != null) {
////            return ResponseEntity.ok(updatedAnnouncement);
////        } else {
////            return ResponseEntity.notFound().build();
////        }
////    }
//
//    // DELETE
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Integer id){
//        reqAnnouncementService.deleteAnnouncement(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    //Soft DELETE
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> SoftdeleteAnnouncement(@PathVariable Integer id){
//        reqAnnouncementService.softDeleteAnnouncement(id);
//        return ResponseEntity.noContent().build();
//    }
//
//
//}
