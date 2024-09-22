package com.ace.service;

import com.ace.dto.*;
import com.ace.entity.Group;
import com.ace.entity.Announcement;
import com.ace.entity.Staff;
import com.ace.repository.GroupRepository;
import com.ace.entity.StaffNotedAnnouncement;
import com.ace.repository.AnnouncementRepository;
import com.ace.repository.NotedRepository;
import com.ace.repository.StaffRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Lazy
public class StaffService implements UserDetailsService {
    private final StaffRepository staffRepository;
    private final AnnouncementRepository announcement_repo;
    private final NotedRepository notedRepository;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
private final GroupRepository groupRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;



    public StaffService(StaffRepository staffRepository,AnnouncementRepository announcement_repo,NotedRepository notedRepository, GroupRepository groupRepository) {
        this.staffRepository = staffRepository;
        this.announcement_repo = announcement_repo;
        this.notedRepository = notedRepository;
        this.groupRepository = groupRepository;
    }


    public List<Staff> findStaffsByIds(List<Integer> ids) {
        return staffRepository.findStaffsByIds(ids);
    }

    public List<String> findStaffsChatIdByIds(List<Integer> ids) {
        return staffRepository.findStaffsChatIdByIds(ids);
    }



    public Page<StaffDTO> getStaffs(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Staff> outputStaff = staffRepository.findAll(pageRequest);

        // Map each Staff entity to StaffGroupDTO
        List<StaffDTO> staffDtos = outputStaff.getContent().stream()
                .map(staff -> modelMapper.map(staff, StaffDTO.class))
                .collect(Collectors.toList());

        // Create a Page<StaffGroupDTO> from the list of DTOs
        return new PageImpl<>(staffDtos, pageRequest, outputStaff.getTotalElements());
    }


    public Page<StaffDTO> searchStaffs(String searchTerm, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Staff> outputStaff = staffRepository.searchByTerm(searchTerm, pageRequest);

        // Map each Staff entity to StaffGroupDTO
        List<StaffDTO> staffDtos = outputStaff.getContent().stream()
                .map(staff -> modelMapper.map(staff, StaffDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(staffDtos, pageRequest, outputStaff.getTotalElements());
    }

    public Staff findByEmail(String email) {
        return staffRepository.findByEmail(email);
    }



    public List<NotedResponseDTO> getNotedStaffList(Integer announcementId) {
        return staffRepository.getNotedStaffByAnnouncement(announcementId);
    }

    public List<UnNotedResponseDTO> getUnNotedStaffListWithGroup(Integer announcementId) {
        return staffRepository.getUnNotedStaffByAnnouncementWithGroup(announcementId);
    }

    public List<UnNotedResponseDTO> getUnNotedStaffList(Integer announcementId) {
        return staffRepository.getUnNotedStaffByAnnouncementWithEach(announcementId);
    }

    public List<Staff> getStaffByPositionId(Integer positionId) {
        return staffRepository.findByPositionId(positionId);
    }

    public Staff getStaffByStaffId(String staffId) {
        return staffRepository.findByCompanyStaffId(staffId);
    }

    public Staff findById(Integer id) {
        return staffRepository.findById(id).orElseThrow();
    }


    public Staff authenticate(String staffId, String password) {
        Staff staff = staffRepository.findByCompanyStaffId(staffId);
        if (staff != null && passwordEncoder.matches(password, staff.getPassword())) {
            return staff;
        }
        return null;
    }

    public List<Staff> findStaffByAnnouncementId(Integer announcementId){
        return staffRepository.findStaffByAnnouncementId(announcementId);
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

    public void updatePassword(PasswordResponseDTO dto) {
        Staff user = staffRepository.findByEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        staffRepository.save(user);
    }

    public Optional<Staff> findByChatId(String chatId) {
        return staffRepository.findByChatId(chatId);
    }

    public void saveChatId(String chatId, String email) {
        Staff user = staffRepository.findByEmail(email);
        user.setChatId(chatId);
        staffRepository.save(user);
    }

    public List<String> getAllChatIds() {
        return staffRepository.findAllChatIds();
    }

    public List<StaffGroupDTO> getStaffListForGroup() {
        return staffRepository.getStaffListForGroup();
    }

    public void addStaff(Staff staff) {
        staffRepository.save(staff);
        Group companyGroup = groupRepository.findByName(staff.getCompany().getName());
        if (companyGroup != null) {
            companyGroup.getStaff().add(staff);
            groupRepository.save(companyGroup);
        }

        Group departmentGroup = groupRepository.findByName(staff.getDepartment().getName() + " (" + staff.getCompany().getName() + ")");
        if (departmentGroup != null) {
            departmentGroup.getStaff().add(staff);
            groupRepository.save(departmentGroup);
        }
    }

    public List<StaffResponseDTO> getStaffList() {
        return staffRepository.getStaffList();
    }

    public List<ActiveStaffResponseDTO> getActiveStaffList() {
        return staffRepository.getActiveStaffList();
    }

    public List<StaffResponseDTO> getHRStaffList(){
        return staffRepository.getHRStaffList();
    }

    public void save(Staff  staff){
         staffRepository.save(staff);
    }

    public Staff getHRMainStaff(String position){
        return staffRepository.findByPosition(position);
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

    //Method to get staff summary count
    public StaffSummaryDTO getStaffSummary() {
        return staffRepository.getStaffSummary();
    }

    // Method to get announcement by staff id desc
    public List<AnnouncementListDTO> getAnnouncementsForStaff(int staffId) {
        List<Announcement> announcements = announcement_repo.findAnnouncementsByStaffId(staffId);
        return announcements.stream()
                .map(a -> new AnnouncementListDTO(a.getId(), a.getTitle(),a.getDescription(), a.getCreateStaff().getName(), a.getCategory().getName(), a.getStatus(),  a.getCreated_at(), a.getScheduleAt(), a.getGroupStatus(), a.getFile()))
                .collect(Collectors.toList());
    }

    //Method to update profile photo
    public Staff updateStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    //Method to change Pw in profile
    public String changeOldPassword(ChangePasswordRequest request) {
        System.out.println("Searching for staff with ID: " + request.getStaffId());
        Staff staff = staffRepository.findByCompanyStaffId(request.getStaffId());

        if (staff != null) {
            System.out.println("Staff found: " + staff.getName());
            if (!passwordEncoder.matches(request.getOldPassword(), staff.getPassword())) {
                return "Old password is incorrect";
            }
            staff.setPassword(passwordEncoder.encode(request.getNewPassword()));
            staffRepository.save(staff);
            return "Password changed successfully";
        } else {
            return "Staff not found";
        }
    }

    public void activateStaff(Integer id){
        Optional<Staff> staff =staffRepository.findById(id);
        staff.get().setStatus("active");
        staffRepository.save(staff.get());
    }

    public void inActivateStaff(Integer id){
        Optional<Staff> staff =staffRepository.findById(id);
        staff.get().setStatus("inactive");
        staffRepository.save(staff.get());
    }
}
