package com.ace.controller;

import com.ace.dto.GroupDTO;
import com.ace.dto.GroupResponseDTO;
import com.ace.entity.Group;
import com.ace.entity.Staff;
import com.ace.service.GroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                .map(group -> {
                    // Extract staff names from Group entity
                    List<String> staffNames = group.getStaff().stream()
                            .map(Staff::getName)
                            .collect(Collectors.toList());

                    // Create a GroupDTO with the extracted staff names
                    return new GroupDTO(
                            group.getId(),
                            group.getName(),
                            group.getStatus (),
                            group.getCreatedAt(),
                            staffNames
                    );
                })
                .collect(Collectors.toList());

        // Return the list of GroupDTOs in the response
        return ResponseEntity.ok().body(groupDTOs);
    }

    @GetMapping("HR/{id}")
    public ResponseEntity<List<GroupDTO>> getGroupsHR(@PathVariable("id") Integer id) {
        List<GroupDTO> groups = groupService.getGroupsByHR(id);
        return ResponseEntity.ok().body(groups);
    }

    @GetMapping("HR/{id}")
    public ResponseEntity<List<GroupResponseDTO>> getGroupsHR(@PathVariable("id") Integer id) {
        List<GroupResponseDTO> groups = groupService.getGroupsByHR(id);
        return ResponseEntity.ok().body(groups);
    }

    @PostMapping("/create")
    public ResponseEntity<String> addGroup(
            @RequestParam String name,
            @RequestBody List<Integer> userIds
            ){
//        if (name.isEmpty()){
//            return "Group name is Empty";
//        }
        Group existGroup = groupService.findByName(name);
        if(existGroup == null){
        groupService.createGroup(name,userIds);
            return  ResponseEntity.ok("Create Successfully");
        }else {
            return ResponseEntity.ok("Group name is already exist.");
        }
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
