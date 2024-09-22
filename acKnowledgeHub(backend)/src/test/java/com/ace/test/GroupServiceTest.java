package com.ace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.ace.entity.Group;
import com.ace.entity.Staff;
import com.ace.repository.GroupRepository;
import com.ace.repository.StaffRepository;
import com.ace.service.GroupService;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;

@SpringBootTest
public class GroupServiceTest {

	@Mock
	GroupRepository groupRepository;

	@Mock
	StaffRepository staffRepository;

	@InjectMocks
	GroupService groupService;

	@Test
	public void testGetAllGroups() {
		Group group1 = new Group();
		group1.setName("Group one ");
		Group group2 = new Group();
		group2.setName("Group two");

		List<Group> allGroups = Arrays.asList(group1, group2);
		when(groupRepository.findAll()).thenReturn(allGroups);
		List<Group> result = groupService.getAllGroups();

		assertNotNull(result);
		assertTrue(result.size() >= 2);
	}

	@Test
	void findGroupsByIdsTest() {
		List<Integer> groupIds = Arrays.asList(1, 2);
		Group group1 = new Group();
		group1.setId(1);
		group1.setName("Group one ");

		Group group2 = new Group();
		group2.setId(2);
		group2.setName("Group two");

		List<Group> groupList = Arrays.asList(group1, group2);
		when(groupRepository.findGroupsByIds(groupIds)).thenReturn(groupList);

		List<Group> result = groupService.findGroupsByIds(groupIds);

		assertNotNull(result);
		assertTrue(result.size() == 2);
	}

//	@Test
//	void createGroup() {
//		
//		Staff staff1 = new Staff();
//        staff1.setId(1);
//
//        Staff staff2 = new Staff();
//        staff2.setId(2);
//        
//        List<Integer> staffIds = Arrays.asList(1, 2);
//        
//        when(staffRepository.findById(1)).thenReturn(Optional.of(staff1));
//        when(staffRepository.findById(2)).thenReturn(Optional.of(staff2));
//        
//        List<Staff> staffList = Arrays.asList(staff1,staff2);
//        
//		Group group1 = new Group();
//		group1.setName("Group One");
//		group1.setStaff(staffList);
//		when(groupRepository.save(group1)).thenReturn(group1);
//		groupRepository.save(group1);
//		
//		verify(groupRepository).save(any(Group.class));  
//        verify(staffRepository).findById(anyInt()); 
//	}

	@Test
	void createGroupTest() {
		String groupName = "Development Team";
		List<Integer> userIds = Arrays.asList(1, 2);

		Staff staff1 = new Staff();
		staff1.setId(1);

		Staff staff2 = new Staff();
		staff2.setId(2);

		when(staffRepository.findById(1)).thenReturn(Optional.of(staff1));
		when(staffRepository.findById(2)).thenReturn(Optional.of(staff2));

		groupService.createGroup(groupName, userIds);

		verify(groupRepository).save(any(Group.class));
	}

	@Test
	void createGroup_userNotFound_throwsException() {
		// Arrange
		String groupName = "Development Team";
		List<Integer> userIds = Arrays.asList(1, 2);

		// Mocking staff entities
		Staff staff1 = new Staff();
		staff1.setId(1);

		when(staffRepository.findById(1)).thenReturn(Optional.of(staff1));
		when(staffRepository.findById(2)).thenReturn(Optional.empty()); // Simulating user not found

		// Act and Assert
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			groupService.createGroup(groupName, userIds);
		});

		assertEquals("User ID not found : 2", thrown.getMessage()); // Ensure exception message is correct
	}

	@Test
	void testGetGroupById() {
		int id = 1;
		Group group = new Group();
		group.setName("Group one");

		when(groupRepository.findById(id)).thenReturn(Optional.of(group));

		Optional<Group> result = groupService.getGroupById(id);

		assertEquals("Group one", result.get().getName());

	}

	@Test
	void testDeactivateGroup() {
		int groupId = 1;
		Group group = new Group();
		group.setId(groupId);
		group.setName("Group one");
		group.setStatus("active");

		Group group2 = new Group();
		group2.setStatus("inactive");
		when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
		when(groupRepository.save(group2)).thenReturn(null);
		groupService.deactivateGroup(groupId);

		assertEquals("inactive", group.getStatus());
	}

	@Test
	void testDeactivateGroup_notFound() {
		int groupId = 1;
		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			groupService.deactivateGroup(groupId);
		});

		assertEquals("Group not found with ID: 1", thrown.getMessage());
	}

	@Test
	void testReactivateGroup() {
		int groupId = 1;
		Group group = new Group();
		group.setId(groupId);
		group.setName("Group one");
		group.setStatus("inactive");

		Group group2 = new Group();
		group2.setStatus("inactive");
		when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
		when(groupRepository.save(group2)).thenReturn(null);
		groupService.reactivateGroup(groupId);

		assertEquals("active", group.getStatus());
	}

	@Test
	void testReactivateGroup_notFound() {
		int groupId = 1;
		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
			groupService.reactivateGroup(groupId);
		});

		assertEquals("Group not found with ID: 1", thrown.getMessage());
	}

	@Test
	void testUpdateGroup() {
		int groupId = 1;
		String newName = "New Group Name";
		List<Integer> userIds = Arrays.asList(1, 2);
		// Existing group setup
		Group existingGroup = new Group();
		existingGroup.setId(groupId);
		existingGroup.setName("Old Group Name");

		// Existing staff members in the group
		Staff staff1 = new Staff();
		staff1.setId(1);
		staff1.setName("Staff 1");

		Staff staff2 = new Staff();
		staff2.setId(2);
		staff2.setName("Staff 2");

		existingGroup.setStaff(Arrays.asList(staff2)); // Initially, staff3 is part of the group

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(existingGroup));
		when(staffRepository.findById(1)).thenReturn(Optional.of(staff1));
		when(staffRepository.findById(2)).thenReturn(Optional.of(staff2));
		// when(staffRepository.findById(3)).thenReturn(Optional.of(staff3));

		groupService.updateGroup(groupId, newName, userIds);
		assertEquals(newName, existingGroup.getName());

		assertTrue(existingGroup.getStaff().contains(staff1));
		assertTrue(existingGroup.getStaff().contains(staff2));

		// assertFalse(existingGroup.getStaff().contains(staff3));

		verify(groupRepository).save(existingGroup);
	}

	@Test
	void testUpdateGroup_whenGroupDoesNotExist() {
		int groupId = 1;
		String name = "New Group Name";
		List<Integer> userIds = Arrays.asList(1, 2);

		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			groupService.updateGroup(groupId, name, userIds);
		});

		assertEquals("Group not found with ID: " + groupId, exception.getMessage());
	}

	@Test
	void testUpdateGroup_withNullUserIds() {
		// Setup
		int groupId = 1;
		String name = "New Group Name";
		List<Integer> userIds = null;

		Group existingGroup = new Group();
		existingGroup.setId(groupId);
		existingGroup.setName("Old Group Name");

		Staff staff1 = new Staff();
		staff1.setName("staff 1");
		existingGroup.setStaff(Arrays.asList(staff1)); // Current group members

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(existingGroup));

		groupService.updateGroup(groupId, name, userIds);

		assertEquals(name, existingGroup.getName());
		assertTrue(existingGroup.getStaff().contains(staff1)); // No users should be removed
		verify(groupRepository).save(existingGroup);
	}

	@Test
	void TestGetStaffsByGroupId() {
		Staff staff1 = new Staff();
		staff1.setName("Mg Mg");
		Staff staff2 = new Staff();
		staff2.setName("Tun Tun");
		Group group = new Group();
	    group.setStaff(Arrays.asList(staff1, staff2));
		when(groupRepository.findById(1)).thenReturn(Optional.of(group));
		List<Staff> result = groupService.getStaffsByGroupId(1);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(staff1, result.get(0));
		assertEquals(staff2, result.get(1));

		verify(groupRepository, times(1)).findById(1);
		verifyNoMoreInteractions(groupRepository);
	}

	@Test
	void testGetStaffsByGroupId_GroupNotFound() {
		when(groupRepository.findById(1)).thenReturn(Optional.empty());

		// Assert that an exception is thrown
		Exception exception = assertThrows(RuntimeException.class, () -> {
			groupService.getStaffsByGroupId(1);
		});

		assertEquals("Group not found", exception.getMessage());

		verify(groupRepository, times(1)).findById(1);
		verifyNoMoreInteractions(groupRepository);
	}

}
