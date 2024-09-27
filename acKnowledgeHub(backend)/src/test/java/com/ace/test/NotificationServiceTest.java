package com.ace.test;

import com.ace.dto.NotificationDTO;
import com.ace.entity.Announcement;
import com.ace.entity.Notification;
import com.ace.repository.AnnouncementRepository;
import com.ace.repository.GroupRepository;
import com.ace.repository.NotificationRepository;
import com.ace.repository.StaffRepository;
import com.ace.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {

    }
    @Test
    void testSaveNotification() {
        Notification notification = new Notification();
        notification.setId(1);
        notification.setDescription("Test Notification");

        // Act
        notificationService.saveNotification(notification);

        // Assert
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testGetNotificationsByStaffId() {
        int staffId = 1;

        // Prepare mock data
        Notification notification = new Notification();
        notification.setId(1);
        notification.setDescription("Test Notification");
        notification.setAnnouncement(new Announcement());
        notification.getAnnouncement().setId(2);

        when(notificationRepository.findByStaffId(staffId)).thenReturn(List.of(notification));
        when(announcementRepository.findAnnouncementsByStaffId(staffId)).thenReturn(Collections.emptyList());
        when(groupRepository.findGroupsByAnnouncementIds(Collections.singletonList(2))).thenReturn(Collections.emptyList());

        // Act
        List<NotificationDTO> result = notificationService.getNotificationsByStaffId(staffId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(notification.getDescription(), result.get(0).getDescription());
    }
    @Test
    void testMarkNotificationsAsInactive() {
        Notification notification = new Notification();
        notification.setId(1);
        notification.setStatus("active");

        when(notificationRepository.findAllById(Collections.singletonList(notification.getId())))
                .thenReturn(List.of(notification));

        // Act
        notificationService.markNotificationsAsInactive(Collections.singletonList(notification.getId()));

        // Assert
        assertEquals("inactive", notification.getStatus());
        verify(notificationRepository, times(1)).saveAll(List.of(notification));
    }

    @Test
    void testSendNotification() {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setStaffId("1");

        // Act
        notificationService.sendNotification(notificationDTO);

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend("/topic/notification/1", notificationDTO);
    }

}
