//package com.ace.service;
//
//
//import com.ace.entity.AnnouncementForReq;
//import com.ace.entity.ReqAnnouncement;
//import com.ace.repository.AnnouncementForReqRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class AnnouncementForReqService {
//    private final AnnouncementForReqRepository announcementForReqRepository;
//
//    public AnnouncementForReqService(AnnouncementForReqRepository announcementForReqRepository) {
//        this.announcementForReqRepository = announcementForReqRepository;
//    }
//
//    //get all
//    public List<AnnouncementForReq> getAllAnnouncementForReq(){
//        return announcementForReqRepository.findAll();
//    }
//
//    //get Announcement For Req by id
//    public AnnouncementForReq getById(Integer id){
//        return announcementForReqRepository.findById(id).orElseThrow();
//    }
//
//    //post AnnouncementForReq
//    public AnnouncementForReq createAnnouncementForReq(AnnouncementForReq reqannounce){
//        return  announcementForReqRepository.save(reqannounce);
//    }
//
////    //edit AnnouncementForReq
////    public AnnouncementForReq editAnnouncementForReq(Integer id, AnnouncementForReq reqAnnouncement){
////        AnnouncementForReq checkAnnouncement = getById(id);
////        if(checkAnnouncement != null){
////              checkAnnouncement.setId(reqAnnouncement.getId());
////            checkAnnouncement.setDescription(reqAnnouncement.getDescription());
////            return announcementForReqRepository.save(reqAnnouncement);
////        }
////        return new AnnouncementForReq();
////    }
//
//    //delete request announcement
//    public void deleteAnnouncementForReq(Integer id){
//        announcementForReqRepository.deleteById(id);
//    }
//
//}
