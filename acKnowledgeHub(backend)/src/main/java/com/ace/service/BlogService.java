package com.ace.service;

import com.ace.controller.AnnouncementController;
import com.ace.dto.NotificationDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Group;
import com.ace.entity.Notification;
import com.ace.entity.Staff;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogService {

    private final AnnouncementService announcementService;
    private final PostSchedulerService postSchedulerService;
    private final BotService botService;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final StaffService staffService;
    private final GroupService groupService;
    private final NotificationService notificationService;

    @Autowired
    public BlogService(AnnouncementService announcementService, PostSchedulerService postSchedulerService, BotService botService, CloudinaryService cloudinaryService, EmailService emailService, StaffService staffService, GroupService groupService, NotificationService notificationService) {
        this.announcementService = announcementService;
        this.postSchedulerService = postSchedulerService;
        this.botService = botService;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.staffService = staffService;
        this.groupService = groupService;
        this.notificationService = notificationService;
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
            byte updateStatus  = 0;
            MultipartFile file = cloudinaryService.getFileAsMultipart(announcement.getFile());
            Optional<Announcement> announcementForFileNameCheck = announcementService.getAnnouncementById(announcement.getId());
            Pattern pattern = Pattern.compile("_V(\\d+)");
            Matcher matcher = pattern.matcher(announcementForFileNameCheck.get().getFile());
            if(matcher.find()){
                Integer versionNumber = Integer.valueOf(matcher.group(1));
                if(versionNumber > 1){
                    updateStatus = 1;
                    System.out.println("it come here");
                }
            }
            sendTelegramAndEmail(announcement.getStaff(), announcement.getGroup(), file, announcement.getId(), announcement.getGroupStatus(),updateStatus);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendTelegramAndEmail(List<Staff> staffForAnnounce, List<Group> groupsForAnnounce, MultipartFile file, Integer announcementId, byte groupStatus,byte updateStatus){
       try{
           int count = 1;
           Announcement announcementForNoti = announcementService.getAnnouncementById(announcementId).orElseThrow();
           if (groupStatus != 1) {
               for (Staff AnnounceStaff : staffForAnnounce) {
                   if (AnnounceStaff != null) {
                       if(AnnounceStaff.getChatId() != null){
                           botService.sendFile(AnnounceStaff.getChatId(), file, announcementId);
                       }
                   }
                   if (AnnounceStaff.getEmail() != null && !AnnounceStaff.getEmail().isEmpty()) {
                    if(updateStatus > 0){
                        emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Updated Announcement", file, file.getOriginalFilename(),announcementId);
                    }else{
                        emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Have A New Announcement", file, file.getOriginalFilename(),announcementId);
                    }
                   }
                   String description;
                   if(updateStatus > 0){
                       description = announcementForNoti.getCreateStaff().getName()+" Updated "+announcementForNoti.getTitle()+" Announcement!Check It Out!";
                   }else{
                       description = announcementForNoti.getCreateStaff().getName()+" Created New Announcement!Check It Out!";
                   }
                   System.out.println(announcementForNoti);
                   Notification notification = createNotification(announcementForNoti, AnnounceStaff, description);
                   notificationService.sendNotification(convertToDTO(notification));
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
                            if(updateStatus > 0){
                                emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Updated Announcement", file, file.getOriginalFilename(),announcementId);
                            }else{
                                emailService.sendFileEmail(AnnounceStaff.getEmail(), "We Have a Announcement", file, file.getOriginalFilename(),announcementId);

                            }
                           }
                           String description;
                           if(updateStatus > 0){
                               description = announcementForNoti.getCreateStaff().getName()+" Updated "+announcementForNoti.getTitle()+" Announcement!Check It Out!";
                           }else{
                               description = announcementForNoti.getCreateStaff().getName()+" Created New Announcement!Check It Out!";
                           }
                           Notification notification = createNotification(announcementForNoti, AnnounceStaff, description);
                           notificationService.sendNotification(convertToDTO(notification));
                       }
                   }
               }
           }
       }catch(Exception e){
           System.out.println(e);
       }
    }

    public Notification createNotification(Announcement announcement, Staff staff, String description) {
        try{
            Notification notification = new Notification();
            notification.setDescription(description);
            notification.setStaff(staff);
            String url = "/acknowledgeHub/announcement/detail/";
            notification.setChecked(false);
            notification.setUrl(url+Base64.getEncoder().encodeToString(announcement.getId().toString().getBytes()));
            notification.setAnnouncement(announcement);
            notificationService.saveNotification(notification);
            return notification;
        }catch (Exception e){
            System.out.println(e);
        }
        return new Notification();
    }

    public NotificationDTO convertToDTO(Notification notification) {
       try{
           List<Integer> groupIds = notification.getAnnouncement().getGroup() != null ? notification.getAnnouncement().getGroup().stream()
                   .map(Group::getId)
                   .collect(Collectors.toList()): new ArrayList<>();
           String staffId = String.valueOf((notification.getStaff().getId()));
           System.out.println("staff id is " +staffId);
           String status = notification.getStatus() != null ? notification.getStatus() : "unknown";
           return new NotificationDTO(
                   notification.getId(),
                   notification.getAnnouncement().getTitle(),
                   notification.getDescription(),
                   staffId,
                   notification.isChecked(),
                   notification.getUrl(),
                   notification.getCreatedAt(),
                   notification.getAnnouncement().getId(),
                   groupIds,
                   status
           );
       }catch(Exception e){
           System.out.println(e);
       }
       return new NotificationDTO();
    }
}
