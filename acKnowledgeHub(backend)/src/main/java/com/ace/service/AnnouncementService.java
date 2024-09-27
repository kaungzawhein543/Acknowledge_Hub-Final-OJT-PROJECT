package com.ace.service;

import com.ace.dto.*;
import com.ace.entity.Announcement;
import com.ace.entity.Group;
import com.ace.entity.Staff;
import com.ace.repository.AnnouncementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AnnouncementService {

    private final AnnouncementRepository announcement_repo;

    public AnnouncementService(AnnouncementRepository announcement_repo){
        this.announcement_repo = announcement_repo;
    }

    // Create a new announcement
    @Transactional
    public Announcement createAnnouncement(Announcement announcement) {
        return announcement_repo.save(announcement);
    }

    public List<String> getAllVersionsByFilePattern(Integer announcementId) {
        Optional<Announcement> announcement =  getAnnouncementById(announcementId);
        String[] pathParts =  announcement.get().getFile().split("/");
        return announcement_repo.getAllVersionsOfAnnouncement(pathParts[2]);
    }

//    public Announcement getLatestVersionByFilePattern(String baseFileName) {
//        List<String> announcements = announcement_repo.getAllVersionsOfAnnouncement(baseFileName);
//        return announcements.isEmpty() ? null : announcements.get(0);  // Return the latest version or null if none found
//    }

    //Find Lastest Version By File
    public Optional<Announcement> findLastByFileName(String file) {
        List<Announcement> announcements = announcement_repo.findAllByFileName(file);
        return Optional.of(announcements.get(announcements.size() - 1));  // Return the last element
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
    public List<AnnouncementListDTO> getPublishedAnnouncements() {
        return announcement_repo.getAnnouncementList(); // Adjust method name based on your repository
    }

    public List<StaffNotedResponseDTO> getStaffNoted(Integer staffId) {
        return announcement_repo.getStaffNoted(staffId);
    }

    public List<AnnouncementResponseListDTO> getStaffUnNoted(Integer staffId) {
        List<AnnouncementResponseListDTO> staffAnnouncements = announcement_repo.getNotNotedStaff(staffId);
        List<AnnouncementResponseListDTO> groupAnnouncements = announcement_repo.getNotNotedStaffGroup(staffId);
        List<AnnouncementResponseListDTO> combinedAnnouncements = new ArrayList<>(staffAnnouncements);
        combinedAnnouncements.addAll(groupAnnouncements);
        for(AnnouncementResponseListDTO announcemenId : combinedAnnouncements){{
            System.out.println(announcemenId.getId());
        }}
        return combinedAnnouncements;
    }

    public List<AnnouncementResponseListDTO> getStaffAnnouncement(Integer staffId) {
        List<AnnouncementResponseListDTO> staffAnnouncements = announcement_repo.getStaffAnnouncement(staffId);
        List<AnnouncementResponseListDTO> groupAnnouncements = announcement_repo.getStaffAnnouncementGroup(staffId);
        List<AnnouncementResponseListDTO> combinedAnnouncements = new ArrayList<>(staffAnnouncements);
        combinedAnnouncements.addAll(groupAnnouncements);
        return combinedAnnouncements;
    }

    public List<AnnouncementResponseListDTO> getPendingAnnouncement(){
        return announcement_repo.getPendingAnnouncement();
    }

    public void publishAnnouncement(Integer id){
        announcement_repo.publishAnnouncement(id);
    }

    public List<AnnouncementVersionDTO> getAnnouncementVersion(Integer id){
        String baseFileName = "%" + "Announce" + id + "/%";
        return announcement_repo.getAllVersions(baseFileName);
    }

//    public List<Announcement> getAllVersionsByFilePattern(String baseFileName) {
//        return announcement_repo.getAllVersionsOfAnnouncement(baseFileName);
//    }

//    public Announcement getLatestVersionByFilePattern(String baseFileName) {
//        List<Announcement> announcements = announcement_repo.getAllVersionsOfAnnouncement(baseFileName);
//        return announcements.isEmpty() ? null : announcements.get(0);  // Return the latest version or null if none found
//    }

    public List<RequestAnnouncementResponseDTO> getRequestAnnouncements(){
        return announcement_repo.getRequestAnnouncement();
    }

    public void approvedRequestAnnouncement(Integer id){
         announcement_repo.approvedRequestAnnouncement(id);
    }

    public void rejectRequestAnnouncement(Integer id){
        announcement_repo.rejectRequestAnnouncement(id);
    }
    //Method to get the staffnotedAnnoucement
    public List<AnnouncementStaffCountDTO> getAnnouncementStaffCounts() {
        return announcement_repo.findAnnouncementStaffCounts();
    }

    //Method to get announcement stats card
    public AnnouncementStatsDTO getAnnouncementStats() {
        // Fetch the announcement statistics from the repository
        return announcement_repo.getAnnouncementCounts();
    }

    //Method to get all announcement monthly count
    public List<MonthlyCountDTO> getMonthlyAnnouncementCounts() {
        return announcement_repo.countActiveAnnouncementsByMonth();
    }

    public List<AnnouncementListDTO> getAnnouncementListByStaffRequest(Integer staffId){
        return announcement_repo.getAnnouncementListByStaffRequest(staffId);
    }

    public void cancelPendingAnnouncement(Integer id ){
        announcement_repo.cancelPendingAnnouncement(id);
    }
//
//    public List<Staff> findStaffByAnnouncementId(Integer announcementId) {
//        return announcement_repo.findStaffByAnnouncementId(announcementId);
//    }
//    public List<Group> findGroupsByAnnouncementId(Integer announcementId) {
//        return announcement_repo.findGroupsByAnnouncementId(announcementId);
//    }

}
