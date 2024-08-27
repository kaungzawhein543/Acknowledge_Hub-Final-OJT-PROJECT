package com.ace.service;


import com.ace.entity.ReqAnnouncement;
import com.ace.repository.ReqAnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReqAnnouncementService {

    private final ReqAnnouncementRepository reqAnnouncementRepository;
    public ReqAnnouncementService(ReqAnnouncementRepository reqAnnouncementRepository) {
        this.reqAnnouncementRepository = reqAnnouncementRepository;
    }

    //get all
    public List<ReqAnnouncement> getAllReqAnnouncement(){
        return reqAnnouncementRepository.findAll();
    }

    //get request announcement by id
    public ReqAnnouncement getById(Integer id){
        return reqAnnouncementRepository.findById(id).orElseThrow();
    }

    //post request nnouncement
    public ReqAnnouncement createReqAnnouncement(ReqAnnouncement reqannounce){
        return  reqAnnouncementRepository.save(reqannounce);
    }

    //edit request announcement
    public ReqAnnouncement editReqAnnouncement(Integer id, ReqAnnouncement reqAnnouncement){
        ReqAnnouncement checkAnnouncement = getById(id);
        if(checkAnnouncement != null){
            checkAnnouncement.setId(reqAnnouncement.getId());
            checkAnnouncement.setDescription(reqAnnouncement.getDescription());
            return reqAnnouncementRepository.save(reqAnnouncement);
        }
        return new ReqAnnouncement();
    }

    //delete request announcement
    public void deleteAnnouncement(Integer id){
          reqAnnouncementRepository.deleteById(id);
    }

    //soft delete request announcement
    public void softDeleteAnnouncement(Integer id){
        reqAnnouncementRepository.softDeleteAnnouncement(id);
    }

}
