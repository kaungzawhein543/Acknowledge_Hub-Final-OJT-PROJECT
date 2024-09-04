package com.ace.service;

import com.ace.dto.*;
import com.ace.entity.Announcement;
import com.ace.entity.Staff;
import com.ace.entity.StaffNotedAnnouncement;
import com.ace.repository.AnnouncementRepository;
import com.ace.repository.NotedRepository;
import com.ace.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffService implements UserDetailsService {
    private final StaffRepository staffRepository;
    private final AnnouncementRepository announcement_repo;
    private final NotedRepository notedRepository;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private PasswordEncoder passwordEncoder;


    public StaffService(StaffRepository staffRepository,AnnouncementRepository announcement_repo,NotedRepository notedRepository) {
        this.staffRepository = staffRepository;
        this.announcement_repo = announcement_repo;
        this.notedRepository = notedRepository;
    }

    public Staff findByEmail(String email){
     return staffRepository.findByEmail(email);
    }

    public List<NotedResponseDTO> getNotedStaffList(Integer announcementId){
        return staffRepository.getNotedStaffByAnnouncement(announcementId);
    }

    public List<UnNotedResponseDTO> getUnNotedStaffList(Integer announcementId){
        return staffRepository.getUnNotedStaffByAnnouncement(announcementId);
    }
    public List<Staff> getStaffByPositionId(Integer positionId) {
    return staffRepository.findByPositionId(positionId);
}

    public Staff getStaffByStaffId(String staffId) {
        return staffRepository.findByCompanyStaffId(staffId);
    }

    public Staff findById(Integer id){
        return staffRepository.findById(id). orElseThrow();
    }


    public Staff authenticate(String staffId, String password) {
        Staff staff = staffRepository.findByCompanyStaffId(staffId);
        if (staff != null && passwordEncoder.matches(password, staff.getPassword())) {
            return staff;
        }
        return null;
    }


    public boolean changePassword(String staffId, String oldPassword, String newPassword) {
        Staff staff = staffRepository.findByCompanyStaffId(staffId);
        if (staff != null && passwordEncoder.matches(oldPassword, staff.getPassword())) {
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            staff.setPassword(encodedNewPassword);
            staffRepository.save(staff);
            return true;
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String staffId) throws UsernameNotFoundException {
        Staff user = staffRepository.findByCompanyStaffId(staffId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getCompanyStaffId())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    public Staff findByStaffId(String staffId) {
        return staffRepository.findByCompanyStaffId(staffId);
    }

    public Staff findById(String staffId) {
        Staff staff = staffRepository.findByCompanyStaffId(staffId);
        if (staff != null) {
            return staff;
        }
        return new Staff();
    }

    public void updatePassword(PasswordResponseDTO dto){
        Staff user = staffRepository.findByEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        staffRepository.save(user);
    }

    public Optional<Staff> findByChatId(String chatId){
        return staffRepository.findByChatId(chatId);
    }

    public void saveChatId(String chatId,String email){
        Staff user =  staffRepository.findByEmail(email);
        user.setChatId(chatId);
        staffRepository.save(user);
    }

    public List<String> getAllChatIds(){
        return  staffRepository.findAllChatIds();
    }

    public List<StaffGroupDTO> getStaffListForGroup(){
        return staffRepository.getStaffListForGroup();
    }


    public List<Map<String, Object>> getStaffCountByAnnouncement() {
        return staffRepository.countStaffByAnnouncement();
    }

    public List<Announcement> getAnnouncementsByStaffId(int staffId) {
        return staffRepository.findById(staffId)
                .map(Staff::getAnnouncement)
                .orElse(new ArrayList<>());
    }

    public Map<String, Long> getMonthlyAnnouncementCount(int staffId) {
        List<Announcement> announcements = getAnnouncementsByStaffId(staffId);
        Map<String, Long> monthlyCount = new HashMap<>();

        // Count announcements per month
        for (Announcement announcement : announcements) {
            if (announcement.getScheduleAt() != null) {
                String monthYear = String.format("%d-%02d",
                        announcement.getScheduleAt().getYear(),
                        announcement.getScheduleAt().getMonthValue());
                monthlyCount.put(monthYear, monthlyCount.getOrDefault(monthYear, 0L) + 1);
            }
        }
        return monthlyCount;
    }

    public Map<String, Long> getNotesCountByMonthForStaff(String staffId) {
        // Get the staff member
        Staff staff = findByStaffId(staffId);
        if (staff == null) {
            throw new IllegalArgumentException("Staff not found");
        }

        // Get all notes for the staff
        List<StaffNotedAnnouncement> notedAnnouncements = notedRepository.findByStaff(staff);

        // Collect counts of notes by month
        return notedAnnouncements.stream()
                .filter(note -> {
                    Announcement announcement = note.getAnnouncement();
                    LocalDateTime announcementDate = announcement.getScheduleAt();
                    LocalDateTime notedDate = note.getNotedAt().toLocalDateTime();

                    // Check if the months match
                    return announcementDate.getMonth().equals(notedDate.getMonth())
                            && announcementDate.getYear() == notedDate.getYear();
                })
                .collect(Collectors.groupingBy(
                        note -> {
                            Announcement announcement = note.getAnnouncement();
                            LocalDateTime announcementDate = announcement.getScheduleAt();
                            return MONTH_FORMATTER.format(announcementDate);
                        },
                        Collectors.counting()
                ));
    }



}
