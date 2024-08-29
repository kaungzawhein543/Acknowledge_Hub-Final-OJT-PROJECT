package com.ace.service;

import com.ace.entity.Staff;
import com.ace.entity.StaffHasGroup;
import com.ace.repository.GroupRepository;

import com.ace.entity.Group;
import com.ace.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private StaffRepository staffRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public void createGroup(String name, List<Integer> userIds){
        Group group=new Group();
        group.setName(name);

        for (Integer userId : userIds) {
            Optional<Staff> selectedUser = staffRepository.findById(userId);
            if (selectedUser.isPresent()){
                Staff user= selectedUser.get();
                StaffHasGroup staffGroup=new StaffHasGroup();
                staffGroup.setStaff(user);
                staffGroup.setGroup(group);

//                group.getStaffGroups().add(staffGroup);
            }else {
                throw new IllegalArgumentException("User ID not found : " + userId);
            }
        }
        groupRepository.save(group);
    }
//    public void updateGroup(int groupId,String name,List<Integer> userIds){
//        Optional<Group> selectedGroup= groupRepository.findById(groupId);
//        if (selectedGroup.isPresent()){
//            Group group=selectedGroup.get();
//            group.setName(name);
//
////            Set<StaffHasGroup> currentStaffGroups = new HashSet<>(group.getStaffGroups());
//            Set<Staff> newUsers= new HashSet<>();
//            if (userIds != null) {
//                for (Integer userId : userIds) {
//                    Optional<Staff> selectedUserOpt = staffRepository.findById(userId);
//                    selectedUserOpt.ifPresent(newUsers::add);
//                }
//            }
//            // Add new users to the group
//            Set<StaffHasGroup> usersToAdd = newUsers.stream()
//                    .filter(user -> currentStaffGroups.stream()
//                            .noneMatch(staffGroup -> staffGroup.getStaff().equals(user)))
//                    .map(user -> {
//                        StaffHasGroup staffGroup = new StaffHasGroup();
//                        staffGroup.setStaff(user);
//                        staffGroup.setGroup(group);
//                        return staffGroup;
//                    })
//                    .collect(Collectors.toSet());
//
//            // Remove users who are no longer in the group
//            Set<StaffHasGroup> usersToRemove = currentStaffGroups.stream()
//                    .filter(staffGroup -> !newUsers.contains(staffGroup.getStaff()))
//                    .collect(Collectors.toSet());
//
//            group.getStaffGroups().removeAll(usersToRemove);
//            group.getStaffGroups().addAll(usersToAdd);
//
//            groupRepository.save(group);
//        }else {
//            throw new RuntimeException("Group not found with ID: " + groupId);
//        }
//    }
    public Optional<Group> getGroupById(int groupId) {
        return groupRepository.findById(groupId);
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

}
