package com.ace.service;

import com.ace.dto.AnnouncementDetails;
import com.ace.dto.NotificationDTO;
import com.ace.dto.StaffDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Group;
import com.ace.entity.Notification;
import com.ace.entity.Staff;
import com.ace.repository.AnnouncementRepository;
import com.ace.repository.GroupRepository;
import com.ace.repository.NotificationRepository;
import com.ace.repository.StaffRepository;
import org.hibernate.Hibernate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final AnnouncementRepository announcementRepository;
    private final StaffRepository staffRepository;
    private GroupRepository groupRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final StaffService staffService;
    private final GroupService groupService;

    public NotificationService(NotificationRepository notificationRepository, AnnouncementRepository announcementRepository, StaffRepository staffRepository, GroupRepository groupRepository, SimpMessagingTemplate messagingTemplate, StaffService staffService, GroupService groupService) {
        this.notificationRepository = notificationRepository;
        this.announcementRepository = announcementRepository;
        this.staffRepository = staffRepository;
        this.groupRepository = groupRepository;
        this.messagingTemplate = messagingTemplate;
        this.staffService = staffService;
        this.groupService = groupService;
    }


    public void saveNotifications(List<Notification> notifications) {
        try {
            notifications.forEach(notification -> System.out.println("Saving notification: " + notification));
            notificationRepository.saveAll(notifications);
        } catch (Exception e) {
            System.out.println("Data were not added to the database: " + e.getMessage());
        }
    }

    @Transactional
    public List<NotificationDTO> getNotificationsByStaffId(int staffId) {
        // Fetch notifications
        List<Notification> notifications = notificationRepository.findByStaffId(staffId);

        // Extract announcement IDs
        List<Integer> announcementIds = notifications.stream()
                .map(notification -> notification.getAnnouncement().getId())
                .distinct()
                .collect(Collectors.toList());

        // Fetch announcements
        List<Announcement> announcements = announcementRepository.findAnnouncementsByStaffId(staffId);

        // Fetch groups
        List<Group> groups = groupRepository.findGroupsByAnnouncementIds(announcementIds);

        // Ensure all collections are initialized
        notifications.forEach(notification -> Hibernate.initialize(notification.getAnnouncement().getGroup()));
        announcements.forEach(announcement -> {
            Hibernate.initialize(announcement.getGroup());
            Hibernate.initialize(announcement.getStaff());
        });
        groups.forEach(group -> Hibernate.initialize(group.getAnnouncement()));

        // Map notifications to DTOs
        return notifications.stream()
                .map(notification -> {
                    Announcement announcement = announcements.stream()
                            .filter(a -> a.getId().equals(notification.getAnnouncement().getId()))
                            .findFirst()
                            .orElse(null);

                    List<Group> announcementGroups = groups.stream()
                            .filter(g -> g.getAnnouncement().contains(announcement))
                            .collect(Collectors.toList());

                    NotificationDTO dto = convertToDTO(notification);
                    dto.setAnnouncementDetails(new AnnouncementDetails(announcement, announcementGroups));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void markNotificationsAsInactive(List<Integer> notificationIds) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        for (Notification notification : notifications) {
            notification.setStatus("inactive");
        }
        notificationRepository.saveAll(notifications);
    }

    public void sendNotification(NotificationDTO notificationDTO) {
        messagingTemplate.convertAndSend("/topic/notifications/" + notificationDTO.getStaffId(), notificationDTO);
    }

    private List<Integer> extractGroupIds(Notification notification) {
        return notification.getAnnouncement() != null
                ? notification.getAnnouncement().getGroup().stream()
                .map(Group::getId)
                .collect(Collectors.toList())
                : List.of();
    }


    private NotificationDTO convertToDTO(Notification notification) {
        String title = notification.getAnnouncement() != null ? notification.getAnnouncement().getTitle() : "";
        NotificationDTO dto = new NotificationDTO(
                title,
                notification.getDescription(),
                notification.getStaff() != null ? notification.getStaff().getCompanyStaffId() : null,
                notification.getAnnouncement() != null ? notification.getAnnouncement().getId() : 0,
                extractGroupIds(notification)
        );

        dto.setId(notification.getId());
        dto.setStatus(notification.getStatus());
        return dto;
    }

}
