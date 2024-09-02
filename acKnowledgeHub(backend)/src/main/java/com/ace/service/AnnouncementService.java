package com.ace.service;

import com.ace.entity.Announcement;
import com.ace.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcement_repo;

    public AnnouncementService(AnnouncementRepository announcement_repo){
        this.announcement_repo = announcement_repo;
    }

    // Create a new announcement
    public Announcement createAnnouncement(Announcement announcement) {
        return announcement_repo.save(announcement);
    }

    // Read an announcement by ID
    public Optional<Announcement> getAnnouncementById(Integer id) {
        return announcement_repo.findById(id);
    }

    // Read all announcements
    public List<Announcement> getAllAnnouncements() {
        return announcement_repo.findAll();
    }

    // Update an existing announcement
    public Announcement updateAnnouncement(Integer id, Announcement announcementDetails) {
        Optional<Announcement> announce = announcement_repo.findById(id);
        Announcement newAnnouncement = new Announcement();
        if(announce.isPresent()){
            newAnnouncement = announce.get();
            newAnnouncement.setId(id);
            newAnnouncement.setTitle(announcementDetails.getTitle());
            newAnnouncement.setDescription(announcementDetails.getDescription());
            newAnnouncement.setFile(announcementDetails.getFile());
            newAnnouncement.setCompany(announcementDetails.getCompany());
            newAnnouncement.setCategory(announcementDetails.getCategory());
            newAnnouncement.setCreateStaff(announcementDetails.getCreateStaff());
            Announcement announcement = announcement_repo.save(newAnnouncement);
            System.out.println(id);
            return announcement;
        }else{
            return new Announcement();
        }
    }

    //update file url in data
    public Announcement updateFileUrl(Announcement announcement) {
        Optional<Announcement> announce = announcement_repo.findById(announcement.getId());
        if (announce.isPresent()) {
            Announcement newAnnouncement = announce.get();
            newAnnouncement.setFile(announcement.getFile());
            return announcement_repo.save(newAnnouncement); // Return the updated announcement
        } else {
            return new Announcement(); // Return a new announcement if the ID does not exist
        }
    }


    // Delete an announcement by ID (Soft)
    public void deleteAnnouncement(Integer id) {
        announcement_repo.softDeleteAnnouncement(id);
    }

    // Method to get published announcements
    public List<Announcement> getPublishedAnnouncements() {
        return announcement_repo.findByStatus("active"); // Adjust method name based on your repository
    }

    public List<Announcement> findAnnouncementsDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return announcement_repo.findByScheduleAtDate(startDateTime, endDateTime);
    }
}
