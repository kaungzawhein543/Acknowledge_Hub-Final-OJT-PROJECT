package com.ace.service;

import com.ace.dto.AnnouncementDetails;
import com.ace.dto.NotificationDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Group;
import com.ace.entity.Notification;
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


    public void saveNotification(Notification notification) {
        try {
            notificationRepository.save(notification);

        } catch (Exception e) {
            System.out.println("Data was not added to the database: " + e.getMessage());
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
        messagingTemplate.convertAndSend("/topic/notification/" + notificationDTO.getStaffId(), notificationDTO);
    }

    public List<Integer> extractGroupIds(Notification notification) {
        return notification.getAnnouncement() != null
                ? notification.getAnnouncement().getGroup().stream()
                .map(Group::getId)
                .collect(Collectors.toList())
                : List.of();
    }


    public NotificationDTO convertToDTO(Notification notification) {
        String title = notification.getAnnouncement() != null ? notification.getAnnouncement().getTitle() : "";NotificationDTO dto = new NotificationDTO(
                notification.getId(),
                title,
                notification.getDescription(),
                notification.getStaff() != null ? notification.getStaff().getCompanyStaffId() : null,
                notification.isChecked(),
                notification.getUrl(),
                notification.getCreatedAt(),
                notification.getAnnouncement() != null ? notification.getAnnouncement().getId() : 0,
                extractGroupIds(notification),
                notification.getStatus() != null ? notification.getStatus() : "unknown"

        );
        dto.setId(notification.getId());
        return dto;
    }


    public List<NotificationDTO> updateNotificationCheck(Integer notificationId,Integer staffId){
        notificationRepository.updateChecked(notificationId);
        return getNotificationsByStaffId(staffId);
    }
}
