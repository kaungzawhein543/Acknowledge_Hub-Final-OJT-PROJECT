package com.ace.service;

import com.ace.entity.Announcement;
import com.ace.entity.Staff;
import com.ace.entity.StaffNotedAnnouncement;
import com.ace.repository.NotedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserNotedAnnouncementService {
    private final NotedRepository notedRepository;

    public UserNotedAnnouncementService(NotedRepository notedRepository) {
        this.notedRepository = notedRepository;
    }

//    public Optional<StaffNotedAnnouncement> checkNotedOrNot(Staff user, Announcement announcement){
//       return  notedRepository.findByStaffAndAnnouncement(user,announcement);
//    }
    public void save(StaffNotedAnnouncement staffNotedAnnouncement){
        notedRepository.save(staffNotedAnnouncement);
    }
}
