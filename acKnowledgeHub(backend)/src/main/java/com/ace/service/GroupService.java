package com.ace.service;

import com.ace.dto.GroupDTO;
import com.ace.dto.StaffDTO;
import com.ace.entity.Staff;
import com.ace.repository.GroupRepository;

import com.ace.entity.Group;
import com.ace.repository.StaffRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final StaffRepository staffRepository;

    public GroupService(GroupRepository groupRepository, StaffRepository staffRepository) {
        this.groupRepository = groupRepository;
        this.staffRepository = staffRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    public List<Group> findGroupsByIds(List<Integer> ids){
        return groupRepository.findGroupsByIds(ids);
    }

    public void createGroup(String name, List<Integer> userIds){
        Group group=new Group();
        group.setName(name);

        for (Integer userId:userIds){
            Optional<Staff> selectedUser=staffRepository.findById(userId);
            if (selectedUser.isPresent()){
                Staff user= selectedUser.get();
                group.getStaff().add(user);
            }else {
                throw new IllegalArgumentException("User ID not found : " + userId);
            }
        }
        groupRepository.save(group);
    }
    public void updateGroup(int groupId,String name,List<Integer> userIds){
        Optional<Group> selectedGroup= groupRepository.findById(groupId);
        if (selectedGroup.isPresent()){
            Group group=selectedGroup.get();
            group.setName(name);

            Set<Staff> newUsers= new HashSet<>();
            if (userIds !=null){
                for (Integer userId:userIds){
                    Optional<Staff> selectedUsers=staffRepository.findById(userId);
                    selectedUsers.ifPresent(newUsers::add);
                }
            }
            Set<Staff> currentUser=new HashSet<>(group.getStaff());

            Set<Staff> usersToAdd=new HashSet<>(newUsers);
            usersToAdd.removeAll(currentUser);

            Set<Staff> usersToRemove= new HashSet<>(currentUser);
            usersToRemove.removeAll(newUsers);

            group.getStaff().addAll(usersToAdd);
            group.getStaff().removeAll(usersToRemove);

            groupRepository.save(group);
        }else {
            throw new RuntimeException("Group not found with ID: " + groupId);
        }
    }
    public Optional<GroupDTO> getGroupById(int groupId) {
        return groupRepository.findById(groupId).map(this::convertToGroupDTO);
    }
    public void deactivateGroup(int groupId) {
        Optional<Group> selectedGroup = groupRepository.findById(groupId);
        if (selectedGroup.isPresent()) {
            Group group = selectedGroup.get();
            group.setStatus("inactive");
            groupRepository.save(group);
        } else {
            throw new RuntimeException("Group not found with ID: " + groupId);
        }
    }
    public void reactivateGroup(int groupId) {
        Optional<Group> selectedGroup = groupRepository.findById(groupId);
        if (selectedGroup.isPresent()) {
            Group group = selectedGroup.get();
            group.setStatus("active");
            groupRepository.save(group);
        } else {
            throw new RuntimeException("Group not found with ID: " + groupId);
        }
    }

    private GroupDTO convertToGroupDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setStatus(group.getStatus());
        dto.setCreatedAt(group.getCreatedAt());

        List<StaffDTO> staffDTOs = group.getStaff().stream()
                .map(this::convertToStaffDTO)
                .collect(Collectors.toList());
        dto.setStaff(staffDTOs);

        return dto;
    }
    private StaffDTO convertToStaffDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setName(staff.getName());
        dto.setCompanyStaffId(staff.getCompanyStaffId());
        dto.setEmail(staff.getEmail());
        dto.setRole(staff.getRole());

        return dto;
    }
    public List<Group> findGroupsByIds(List<Integer> groupIds) {
        return groupRepository.findAllById(groupIds);
    @Transactional
    public List<Staff> getStaffsByGroupId(Integer groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Explicitly initialize the staff collection
        Hibernate.initialize(group.getStaff());

        return group.getStaff();
    }
}
