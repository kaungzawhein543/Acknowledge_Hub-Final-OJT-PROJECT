package com.ace.test;

import com.ace.dto.AnnouncementListDTO;
import com.ace.dto.AnnouncementResponseListDTO;
import com.ace.dto.AnnouncementVersionDTO;
import com.ace.dto.StaffNotedResponseDTO;
import com.ace.entity.Announcement;
import com.ace.repository.AnnouncementRepository;
import com.ace.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AnnouncementServiceTest {
    @Mock
    private AnnouncementRepository announcement_repo;

    @InjectMocks
    private AnnouncementService announcementService;

    private Announcement announcement;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAnnouncement() {
        Announcement announcement = new Announcement(1, "Title", new Date(), null, "file", "Description",
                false, "active", (byte) 1, null, null, null, null);
        when(announcement_repo.save(any(Announcement.class))).thenReturn(announcement);

        Announcement createdAnnouncement = announcementService.createAnnouncement(announcement);

        assertNotNull(createdAnnouncement);
        assertEquals("Title", createdAnnouncement.getTitle());
        verify(announcement_repo, times(1)).save(announcement);
    }
    @Test
    public void testGetAnnouncementById() {
        Announcement announcement = new Announcement(1, "Title", new Date(), null, "file", "Description",
                false, "active", (byte) 1, null, null, null, null);
        when(announcement_repo.findById(1)).thenReturn(Optional.of(announcement));

        Optional<Announcement> foundAnnouncement = announcementService.getAnnouncementById(1);

        assertTrue(foundAnnouncement.isPresent());
        assertEquals("Title", foundAnnouncement.get().getTitle());
    }

    @Test
    public void testUpdateAnnouncement() {
        Announcement existingAnnouncement = new Announcement(1, "Old Title", new Date(), null, "old_file", "Old Description",
                false, "active", (byte) 1, null, null, null, null);
        Announcement updateDetails = new Announcement(null, "New Title", null, null, "new_file", "New Description",
                false, null, (byte) 1, null, null, null, null);
        when(announcement_repo.findById(1)).thenReturn(Optional.of(existingAnnouncement));
        when(announcement_repo.save(any(Announcement.class))).thenReturn(existingAnnouncement);

        Announcement updatedAnnouncement = announcementService.updateAnnouncement(1, updateDetails);

        assertNotNull(updatedAnnouncement);
        assertEquals("New Title", updatedAnnouncement.getTitle());
        verify(announcement_repo, times(1)).save(existingAnnouncement);
    }

    @Test
    public void testDeleteAnnouncement() {
        doNothing().when(announcement_repo).softDeleteAnnouncement(1);
        announcementService.deleteAnnouncement(1);
        verify(announcement_repo, times(1)).softDeleteAnnouncement(1);
    }
    @Test
    public void testGetPublishedAnnouncements() {
        // Create test data for AnnouncementListDTO
        Integer id = 1;
        String title = "Test Announcement";
        String description = "This is a test announcement description.";
        String createStaff = "staff@example.com";
        String category = "General";
        String status = "Published";
        Date created_at = new Date();
        LocalDateTime scheduleAt = LocalDateTime.now().plusDays(1);
        byte groupStatus = 1;

        List<AnnouncementListDTO> publishedAnnouncements = new ArrayList<>();
        publishedAnnouncements.add(new AnnouncementListDTO(id, title, description, createStaff, category, status, created_at, scheduleAt, groupStatus));

        when(announcement_repo.getAnnouncementList()).thenReturn(publishedAnnouncements);

        // Call the method under test
        List<AnnouncementListDTO> result = announcementService.getPublishedAnnouncements();

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(status, result.get(0).getStatus());
        verify(announcement_repo, times(1)).getAnnouncementList();
    }

    @Test
    public void testGetStaffNoted() {
        Integer staffId = 1;
        Integer id = 1;
        String title = "Important Note";
        String description = "This is a description of the important note.";
        LocalDateTime createdAt = LocalDateTime.now();
        Timestamp notedAt = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        String createStaff = "staff@example.com";

        // Create the list to hold test data
        List<StaffNotedResponseDTO> notedResponses = new ArrayList<>();
        // Populate the list with a properly initialized StaffNotedResponseDTO
        notedResponses.add(new StaffNotedResponseDTO(id, title, description, createdAt, notedAt, createStaff));

        // Mocking the repository method
        when(announcement_repo.getStaffNoted(staffId)).thenReturn(notedResponses);

        // Call the method under test
        List<StaffNotedResponseDTO> result = announcementService.getStaffNoted(staffId);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(description, result.get(0).getDescription());
        verify(announcement_repo, times(1)).getStaffNoted(staffId);
    }

    @Test
    public void testGetStaffUnNoted() {

        Integer staffId = 1;
        Integer id1 = 1;
        Integer id2 = 2;
        String title1 = "Staff UnNoted Announcement 1";
        String description1 = "Details of staff unnoted announcement 1.";
        LocalDateTime createdAt1 = LocalDateTime.now();
        String createStaff1 = "staff1@example.com";
        String category1 = "General";

        String title2 = "Group UnNoted Announcement";
        String description2 = "Details of group unnoted announcement.";
        LocalDateTime createdAt2 = LocalDateTime.now().minusDays(1);
        String createStaff2 = "staff2@example.com";
        String category2 = "Group";


        List<AnnouncementResponseListDTO> staffAnnouncements = new ArrayList<>();
        List<AnnouncementResponseListDTO> groupAnnouncements = new ArrayList<>();


        staffAnnouncements.add(new AnnouncementResponseListDTO(id1, title1, description1, createdAt1, createStaff1, category1));
        groupAnnouncements.add(new AnnouncementResponseListDTO(id2, title2, description2, createdAt2, createStaff2, category2));


        when(announcement_repo.getNotStaffNoted(staffId)).thenReturn(staffAnnouncements);
        when(announcement_repo.getNotStaffNotedGroup(staffId)).thenReturn(groupAnnouncements);

        // Call the method under test
        List<AnnouncementResponseListDTO> result = announcementService.getStaffUnNoted(staffId);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(title1, result.get(0).getTitle());
        assertEquals(title2, result.get(1).getTitle());
        verify(announcement_repo, times(1)).getNotStaffNoted(staffId);
        verify(announcement_repo, times(1)).getNotStaffNotedGroup(staffId);
    }


    @Test
    public void testGetAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        // Populate the list with test data
        announcements.add(new Announcement());

        when(announcement_repo.findAll()).thenReturn(announcements);

        List<Announcement> result = announcementService.getAllAnnouncements();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(announcement_repo, times(1)).findAll();
    }
    @Test
    public void testUpdateFileUrl() {
        Announcement announcementToUpdate = new Announcement();
        announcementToUpdate.setId(1);
        announcementToUpdate.setFile("new_file_url");

        Announcement existingAnnouncement = new Announcement();
        existingAnnouncement.setFile("old_file_url");

        when(announcement_repo.findById(anyInt())).thenReturn(Optional.of(existingAnnouncement));
        when(announcement_repo.save(any(Announcement.class))).thenReturn(existingAnnouncement);

        Announcement updatedAnnouncement = announcementService.updateFileUrl(announcementToUpdate);

        assertNotNull(updatedAnnouncement);
        assertEquals("new_file_url", updatedAnnouncement.getFile());
        verify(announcement_repo, times(1)).findById(1);
        verify(announcement_repo, times(1)).save(existingAnnouncement);
    }

    @Test
    public void testGetLatestVersionByFilePattern() {
        String baseFileName = "Announce1";
        List<Announcement> versions = new ArrayList<>();
        versions.add(new Announcement());

        when(announcement_repo.getAllVersionsOfAnnouncement(baseFileName)).thenReturn(versions);

        Announcement result = announcementService.getLatestVersionByFilePattern(baseFileName);

        assertNotNull(result);
        verify(announcement_repo, times(1)).getAllVersionsOfAnnouncement(baseFileName);
    }

    @Test
    public void testGetStaffAnnouncement() {
        Integer staffId = 1;
        Integer id1 = 1;
        Integer id2 = 2;
        String title1 = "Staff Announcement 1";
        String description1 = "Details of staff announcement 1.";
        LocalDateTime createdAt1 = LocalDateTime.now();
        String createStaff1 = "staff1@example.com";
        String category1 = "General";

        String title2 = "Group Announcement";
        String description2 = "Details of group announcement.";
        LocalDateTime createdAt2 = LocalDateTime.now().minusDays(1);
        String createStaff2 = "staff2@example.com";
        String category2 = "Group";

        // Create the lists to hold test data
        List<AnnouncementResponseListDTO> staffAnnouncements = new ArrayList<>();
        List<AnnouncementResponseListDTO> groupAnnouncements = new ArrayList<>();

        // Populate the lists with properly initialized AnnouncementResponseListDTOs
        staffAnnouncements.add(new AnnouncementResponseListDTO(id1, title1, description1, createdAt1, createStaff1, category1));
        groupAnnouncements.add(new AnnouncementResponseListDTO(id2, title2, description2, createdAt2, createStaff2, category2));

        // Mocking the repository methods
        when(announcement_repo.getStaffAnnouncement(staffId)).thenReturn(staffAnnouncements);
        when(announcement_repo.getStaffAnnouncementGroup(staffId)).thenReturn(groupAnnouncements);

        // Call the method under test
        List<AnnouncementResponseListDTO> result = announcementService.getStaffAnnouncement(staffId);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(title1, result.get(0).getTitle());
        assertEquals(title2, result.get(1).getTitle());
        verify(announcement_repo, times(1)).getStaffAnnouncement(staffId);
        verify(announcement_repo, times(1)).getStaffAnnouncementGroup(staffId);
    }

    @Test
    public void testGetPendingAnnouncement() {

        Integer id = 1;
        String title = "Pending Announcement";
        String description = "Details of the pending announcement.";
        LocalDateTime createdAt = LocalDateTime.now();
        String createStaff = "staff@example.com";
        String category = "Pending";


        List<AnnouncementResponseListDTO> pendingAnnouncements = new ArrayList<>();

        pendingAnnouncements.add(new AnnouncementResponseListDTO(id, title, description, createdAt, createStaff, category));


        when(announcement_repo.getPendingAnnouncement()).thenReturn(pendingAnnouncements);


        List<AnnouncementResponseListDTO> result = announcementService.getPendingAnnouncement();


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        assertEquals(description, result.get(0).getDescription());
        verify(announcement_repo, times(1)).getPendingAnnouncement();
    }

    @Test
    public void testGetAnnouncementVersion() {
        Integer id = 1;
        String title = "Version 1.0";

        List<AnnouncementVersionDTO> versions = new ArrayList<>();

        versions.add(new AnnouncementVersionDTO(id, title));

        when(announcement_repo.getAllVersions("Announce1")).thenReturn(versions);

        List<AnnouncementVersionDTO> result = announcementService.getAnnouncementVersion(id);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(title, result.get(0).getTitle());
        verify(announcement_repo, times(1)).getAllVersions("Announce1");
    }

    @Test
    public void testGetAllVersionsByFilePattern() {
        String baseFileName = "Announce1";
        List<Announcement> versions = new ArrayList<>();
        // Populate the list with test data
        versions.add(new Announcement()); // Add relevant test data here

        when(announcement_repo.getAllVersionsOfAnnouncement(baseFileName)).thenReturn(versions);

        List<Announcement> result = announcementService.getAllVersionsByFilePattern(baseFileName);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(announcement_repo, times(1)).getAllVersionsOfAnnouncement(baseFileName);
    }
}
