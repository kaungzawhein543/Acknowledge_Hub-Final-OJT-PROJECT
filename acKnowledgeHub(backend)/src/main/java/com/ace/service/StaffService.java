package com.ace.service;

import com.ace.dto.*;
import com.ace.entity.Staff;
import com.ace.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService implements UserDetailsService {
    private final StaffRepository staffRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
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
}
