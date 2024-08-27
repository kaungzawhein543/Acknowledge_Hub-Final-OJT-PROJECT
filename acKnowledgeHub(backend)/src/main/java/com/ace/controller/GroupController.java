package com.ace.controller;

import com.ace.entity.Group;
import com.ace.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping("api/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @PostMapping("/create")
    public String addGroup(
            @RequestParam String name,
            @RequestParam List<Integer> userIds
            ){
        if (name.isEmpty()){
            return "Group name is Empty";
        }
        groupService.createGroup(name,userIds);
        return "Create Successfully";
    }

    @PutMapping("/update/{groupId}")
    public void updateGroup(
            @PathVariable int groupId,
            @RequestParam String name,
            @RequestParam List<Integer> userIds
    ) {
        groupService.updateGroup(groupId, name, userIds);
    }
    @GetMapping("/{groupId}")
    public Group getGroup(@PathVariable int groupId){
        return groupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group Id is not Found"+ groupId));
    }

    @DeleteMapping("/softDelete/{groupId}")
    public void deleteGroup(@PathVariable int groupId){
        groupService.deactivateGroup(groupId);
    }

    @PutMapping("/activate/{groupId}")
    public void reactivateGroup(@PathVariable int groupId) {
        groupService.reactivateGroup(groupId);
    }
}
