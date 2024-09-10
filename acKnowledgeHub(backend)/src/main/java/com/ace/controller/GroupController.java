package com.ace.controller;

import com.ace.dto.GroupDTO;
import com.ace.entity.Group;
import com.ace.service.GroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/group")
public class GroupController {

    private final GroupService groupService;
    private final ModelMapper mapper;

    public GroupController(GroupService groupService, ModelMapper mapper) {
        this.groupService = groupService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();

        // Map each Group entity to a GroupDTO
        List<GroupDTO> groupDTOs = groups.stream()
                .map(group -> mapper.map(group, GroupDTO.class))
                .collect(Collectors.toList());

        // Return the list of GroupDTOs in the response
        return ResponseEntity.ok().body(groupDTOs);
    }


    @PostMapping("/create")
    public String addGroup(
            @RequestParam String name,
            @RequestBody List<Integer> userIds
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
    public ResponseEntity<GroupDTO> getGroup(@PathVariable int groupId) {
        return groupService.getGroupById(groupId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group ID not found: " + groupId));
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
