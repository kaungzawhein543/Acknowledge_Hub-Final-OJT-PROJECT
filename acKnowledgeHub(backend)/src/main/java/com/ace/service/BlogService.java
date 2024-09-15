package com.ace.service;

import com.ace.controller.AnnouncementController;
import com.ace.entity.Announcement;
import com.ace.entity.Group;
import com.ace.entity.Staff;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final AnnouncementService announcementService;
    private final PostSchedulerService postSchedulerService;
    private final BotService botService;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final StaffService staffService;
    private final GroupService groupService;

    @Autowired
    public BlogService(AnnouncementService announcementService, PostSchedulerService postSchedulerService, BotService botService, CloudinaryService cloudinaryService, EmailService emailService, StaffService staffService, GroupService groupService) {
        this.announcementService = announcementService;
        this.postSchedulerService = postSchedulerService;
        this.botService = botService;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.staffService = staffService;
        this.groupService = groupService;
    }

    public void createPost(Announcement announcement) {
        postSchedulerService.schedulePost(announcement.getId(), () -> publishPost(announcement.getId()), announcement.getScheduleAt());
    }

    public void updateScheduledPost(Integer postId, LocalDateTime newPublishDateTime) {
        Announcement announcement = announcementService.getAnnouncementById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        if (announcement.isPublished()) {
            throw new IllegalStateException("Cannot reschedule a post that has already been published");
        }
        postSchedulerService.schedulePost(postId, () -> publishPost(postId), newPublishDateTime);
    }

    public void publishPost(Integer postId) {
        Announcement announcement = announcementService.getAnnouncementById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Hibernate.initialize(announcement.getGroup());

        Hibernate.initialize(announcement.getStaff());
        announcement.setPublished(true);
        announcementService.createAnnouncement(announcement);

        try {
            MultipartFile file = cloudinaryService.getFileAsMultipart(announcement.getFile());
            sendTelegramAndEmail(announcement.getStaff(), announcement.getGroup(), file, announcement.getId(), announcement.getGroupStatus());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendTelegramAndEmail(List<Staff> staffForAnnounce, List<Group> groupsForAnnounce, MultipartFile file, Integer announcementId, byte groupStatus){
        if (groupStatus != 1) {
            for (Staff AnnounceStaff : staffForAnnounce) {
                if (AnnounceStaff != null) {
                    botService.sendFile(AnnounceStaff.getChatId(), file, announcementId);
                }
                if (AnnounceStaff.getEmail() != null && !AnnounceStaff.getEmail().isEmpty()) {
                    emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Have a new Announcement", file, file.getOriginalFilename());
                }
            }
        } else {
            for (Group group : groupsForAnnounce) {
                if (group != null) {
                    List<Staff> staffFromGroup = group.getStaff(); // Accessing initialized collection
                    for (Staff AnnounceStaff : staffFromGroup) {
                        if (AnnounceStaff.getChatId() != null) {
                            if(!file.isEmpty() && file != null){
                                if(AnnounceStaff.getChatId() != null){
                                    botService.sendFile(AnnounceStaff.getChatId(), file, announcementId);
                                }
                            }else{
                                System.out.println("File is null or empty");
                            }
                        }
                        if (AnnounceStaff.getEmail() != null && !AnnounceStaff.getEmail().isEmpty()) {
                            emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Have a new Announcement", file, file.getOriginalFilename());
                        }
                    }
                }
            }
        }
    }

}
