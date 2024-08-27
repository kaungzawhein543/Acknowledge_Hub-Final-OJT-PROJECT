package com.ace.service;

import com.ace.entity.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlogService {

    private final AnnouncementService announcementService;
    private final PostSchedulerService postSchedulerService;
    private final BotService botService;

    @Autowired
    public BlogService(AnnouncementService announcementService, PostSchedulerService postSchedulerService, BotService botService) {
        this.announcementService = announcementService;
        this.postSchedulerService = postSchedulerService;
        this.botService = botService;
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
        Announcement announcement = announcementService.getAnnouncementById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        announcement.setPublished(true);
        announcementService.createAnnouncement(announcement);
        //  webSocketController.notifyPostPublished(postId);

    }
}
