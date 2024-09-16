package com.ace.service;

import com.ace.dto.*;
import com.ace.entity.Group;
import com.ace.entity.Staff;
import com.ace.repository.GroupRepository;
import com.ace.repository.StaffRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Lazy
public class StaffService implements UserDetailsService {
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final GroupRepository groupRepository;


    public StaffService(StaffRepository staffRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, GroupRepository groupRepository) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
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
}
